package py.com.volpe.cotizacion.gatherer;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Objects;

/**
 * Más Cambios loads the rates of each branch from a JSON endpoint, the remote code of the branch is the value of the
 * 'sucursal' parameter ('matriz', 'sucursal1', ...).
 * <p>
 * The response is {@code {"cotizaciones":{"1":{"compra":"5850","venta":"5950"},...},"fecha":"..."}}, the prices use
 * '.' as decimal separator. The keys are ids of the site, the page (js/myscript.js) only shows the ones in
 * {@link #CURRENCIES}, the other ids are unknown and are ignored.
 *
 * @author Arturo Volpe
 * @since 2026-10-25
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class MasCambios implements Gatherer {

    static final String URL = "https://mascambios.com.py/api/cotizaciones.php?sucursal=";

    private static final String CODE = "MASCAMBIOS";
    private static final int TIMEOUT = 30_000;
    private static final String MAIN_BRANCH = "matriz";
    /**
     * Some branches publish rates far from the rest of the market (2026-10-25: USD 6950 / 7080 in sucursal4 with the
     * main branch at 5850 / 5950), a rate whose purchase or sale is further than this from the main branch is discarded.
     */
    private static final BigDecimal MAX_DEVIATION = new BigDecimal("0.10");
    /**
     * The ARS is rounded to integers (3 / 4 against 3.60 / 3.85) and it is barely traded, only the absurd values
     * are discarded (2026-10-25: 3.98 / 95 in sucursal6).
     */
    private static final BigDecimal MAX_ARS_DEVIATION = new BigDecimal("0.50");
    /**
     * The id used by the site for each currency, the 3 is the guaraní (always 0).
     */
    private static final Map<String, String> CURRENCIES = new LinkedHashMap<>();

    static {
        CURRENCIES.put("1", "USD");
        CURRENCIES.put("2", "BRL");
        CURRENCIES.put("4", "ARS");
        CURRENCIES.put("5", "EUR");
    }

    private final HTTPHelper helper;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        List<QueryResponse> responses = branches.stream()
                .map(this::queryBranch)
                .filter(Objects::nonNull)
                .toList();
        return discardDeviations(responses);
    }

    /**
     * Compares every branch with the main branch, before anything is stored. Without the main branch there is nothing
     * to compare with and the rates are kept.
     */
    private static List<QueryResponse> discardDeviations(List<QueryResponse> responses) {

        QueryResponse main = responses.stream()
                .filter(qr -> MAIN_BRANCH.equals(qr.getBranch().getRemoteCode()))
                .findFirst()
                .orElse(null);
        if (main == null) {
            log.warn("Más Cambios has no rates of the main branch, the rates of the branches are not checked");
            return responses;
        }

        Map<String, QueryResponseDetail> reference = main.getDetails().stream()
                .collect(Collectors.toMap(QueryResponseDetail::getIsoCode, Function.identity()));
        List<QueryResponse> toRet = new ArrayList<>();
        for (QueryResponse qr : responses) {
            if (qr != main) {
                qr.setDetails(qr.getDetails().stream()
                        .filter(detail -> isCloseTo(qr.getBranch().getRemoteCode(), detail, reference.get(detail.getIsoCode())))
                        .toList());
                if (qr.getDetails().isEmpty()) continue;
            }
            toRet.add(qr);
        }
        return toRet;
    }

    private static boolean isCloseTo(String remoteCode, QueryResponseDetail detail, QueryResponseDetail main) {
        if (main == null) return true;
        BigDecimal maxDeviation = "ARS".equals(detail.getIsoCode()) ? MAX_ARS_DEVIATION : MAX_DEVIATION;
        if (isClose(detail.getPurchase().amount(), main.getPurchase().amount(), maxDeviation)
                && isClose(detail.getSale().amount(), main.getSale().amount(), maxDeviation)) return true;

        log.warn("Más Cambios {} {} {} / {} is too far from the main branch {} / {}, it is discarded", remoteCode,
                detail.getIsoCode(), detail.getPurchase().amount(), detail.getSale().amount(),
                main.getPurchase().amount(), main.getSale().amount());
        return false;
    }

    private static boolean isClose(BigDecimal value, BigDecimal reference, BigDecimal maxDeviation) {
        BigDecimal deviation = value.subtract(reference).abs().divide(reference, 4, RoundingMode.HALF_UP);
        return deviation.compareTo(maxDeviation) <= 0;
    }

    static String getBranchUrl(String remoteCode) {
        return URL + remoteCode;
    }

    /**
     * A branch that fails is skipped, so it doesn't leave the other branches without rates.
     */
    private QueryResponse queryBranch(PlaceBranch branch) {
        try {
            String json = helper.doGet(getBranchUrl(branch.getRemoteCode()), TIMEOUT,
                    Map.of(HttpHeaders.ACCEPT, "application/json"), false);
            QueryResponse qr = new QueryResponse(branch);
            qr.setDetails(parse(json));
            if (qr.getDetails().isEmpty()) {
                log.warn("Más Cambios returned no rates for the branch {}", branch.getRemoteCode());
                return null;
            }
            return qr;
        } catch (AppException | JacksonException | SocketTimeoutException e) {
            log.warn("Can't query the Más Cambios branch {}", branch.getRemoteCode(), e);
            return null;
        }
    }

    private List<QueryResponseDetail> parse(String json) {

        JsonNode rates = mapper.readTree(json).path("cotizaciones");
        List<QueryResponseDetail> toRet = new ArrayList<>();
        CURRENCIES.forEach((id, isoCode) -> {
            JsonNode rate = rates.path(id);
            String purchase = rate.path("compra").asString().trim();
            String sale = rate.path("venta").asString().trim();
            if (purchase.isEmpty() || sale.isEmpty()) return;

            QueryResponseDetail detail = parseDetail(isoCode, purchase, sale);
            // the site uses '0' for the currencies without rate
            if (detail == null || detail.getPurchase().amount().signum() <= 0 || detail.getSale().amount().signum() <= 0) return;

            toRet.add(detail);
        });
        return toRet;
    }

    private static QueryResponseDetail parseDetail(String isoCode, String purchase, String sale) {
        try {
            return new QueryResponseDetail(Quote.of(isoCode, purchase), Quote.of(isoCode, sale));
        } catch (NumberFormatException e) {
            log.warn("Can't parse the {} rate (purchase: '{}', sale: '{}')", isoCode, purchase, sale, e);
            return null;
        }
    }

    @Override
    public String getCode() {
        return CODE;
    }
}

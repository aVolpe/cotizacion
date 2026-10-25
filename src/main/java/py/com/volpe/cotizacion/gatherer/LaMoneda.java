package py.com.volpe.cotizacion.gatherer;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
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

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * La Moneda renders the rates in the home page, but they are loaded from a JSON endpoint by branch, the remote code
 * of the branch is the value of the 'sucursal' parameter ('casa_matriz', 'sucursal_jebai', ...).
 * <p>
 * Each element of 'cotizaciones' is a pair ({@code moneda1}/{@code moneda2}) with the prices as JSON numbers, only
 * the pairs against the guaraní of the currencies in {@link #CURRENCIES} are used: the cross rates (DOLAR/REAL) and
 * the 'DOLAR INUSUAL' are ignored.
 *
 * @author Arturo Volpe
 * @since 2026-10-25
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class LaMoneda implements Gatherer {

    static final String URL = "https://www.lamoneda.com.py/api/cotizaciones.php?sucursal=";

    private static final String CODE = "LAMONEDA";
    private static final String GUARANI = "GUARANI";
    private static final int TIMEOUT = 30_000;
    private static final Map<String, String> CURRENCIES = Map.of(
            "DOLAR", "USD",
            "REAL", "BRL",
            "PESOS", "ARS",
            "EURO", "EUR");

    private final HTTPHelper helper;
    // the prices are read as BigDecimal, a double would change values like 3.65
    private final ObjectMapper mapper = JsonMapper.builder()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .build();

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        return branches.stream()
                .map(this::queryBranch)
                .filter(Objects::nonNull)
                .toList();
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
                log.warn("La Moneda returned no rates for the branch {}", branch.getRemoteCode());
                return null;
            }
            return qr;
        } catch (AppException | JacksonException | SocketTimeoutException e) {
            log.warn("Can't query the La Moneda branch {}", branch.getRemoteCode(), e);
            return null;
        }
    }

    private List<QueryResponseDetail> parse(String json) {

        JsonNode root = mapper.readTree(json);
        List<QueryResponseDetail> toRet = new ArrayList<>();
        if (!root.path("success").asBoolean(false)) return toRet;

        for (JsonNode node : root.path("cotizaciones")) {
            String isoCode = CURRENCIES.get(node.path("moneda1").asString().trim());
            JsonNode purchase = node.path("compra");
            JsonNode sale = node.path("venta");
            if (isoCode == null || !GUARANI.equals(node.path("moneda2").asString().trim())
                    || !purchase.isNumber() || !sale.isNumber()) continue;

            QueryResponseDetail detail = new QueryResponseDetail(Quote.of(isoCode, purchase.decimalValue()),
                    Quote.of(isoCode, sale.decimalValue()));
            // the site uses '0' for the currencies without rate
            if (detail.getPurchase().amount().signum() <= 0 || detail.getSale().amount().signum() <= 0) continue;

            toRet.add(detail);
        }
        return toRet;
    }

    @Override
    public String getCode() {
        return CODE;
    }
}

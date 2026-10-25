package py.com.volpe.cotizacion.gatherer;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The FeCambios site loads the rates of each branch from a JSON list, the remote code of the branch is the prefix
 * of the URL ('pal', 'vmo', 'cde', 'slo').
 * <p>
 * Each element is {@code {"name":"5660","description":"5780","id":"USD"}}: the name is the purchase, the description
 * the sale and the id the ISO code. A branch without data returns every price as '0'.
 *
 * @author Arturo Volpe
 * @since 10/5/18
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class FeCambios implements Gatherer {

    static final String URL = "https://www.fecambios.com.py/";

    private final HTTPHelper helper;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        return branches.stream()
                .map(this::queryBranch)
                .filter(Objects::nonNull)
                .toList();
    }

    static String getBranchUrl(String remoteCode) {
        return URL + remoteCode + "-tasks-list-cot.php";
    }

    /**
     * A branch that fails is skipped, so it doesn't leave the other branches without rates.
     */
    private QueryResponse queryBranch(PlaceBranch branch) {
        try {
            QueryResponse qr = new QueryResponse(branch);
            qr.setDetails(parse(helper.doGet(getBranchUrl(branch.getRemoteCode()))));
            if (qr.getDetails().isEmpty()) {
                log.warn("FeCambios returned no rates for the branch {}", branch.getRemoteCode());
                return null;
            }
            return qr;
        } catch (AppException | JacksonException e) {
            log.warn("Can't get data from branch {}", branch.getRemoteCode(), e);
            return null;
        }
    }

    private List<QueryResponseDetail> parse(String json) {

        List<QueryResponseDetail> toRet = new ArrayList<>();
        for (JsonNode node : mapper.readTree(json)) {
            String isoCode = node.path("id").asString().trim();
            String purchase = node.path("name").asString().trim();
            String sale = node.path("description").asString().trim();
            if (!isoCode.matches("[A-Z]{3}") || purchase.isEmpty() || sale.isEmpty()) continue;

            QueryResponseDetail detail = parseDetail(isoCode, purchase, sale);
            // the site uses '0' for the currencies without rate
            if (detail == null || detail.getPurchase().amount().signum() <= 0 || detail.getSale().amount().signum() <= 0) continue;

            toRet.add(detail);
        }
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
        return "FeCambios";
    }
}

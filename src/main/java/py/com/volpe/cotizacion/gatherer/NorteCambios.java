package py.com.volpe.cotizacion.gatherer;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Norte Cambios renders only the main branch in the page, the other branches are loaded with a Livewire call.
 * <p>
 * To get a branch we replay what the browser does: read the session token and the component snapshot from the
 * home page and then call the 'setActiveBranch' action with the branch id (stored as the remote code).
 *
 * @author Arturo Volpe
 * @since 2026-10-24
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class NorteCambios implements Gatherer {

    static final String URL = "https://www.nortecambios.com.py/";
    static final String UPDATE_URL = URL + "livewire/update";

    private static final String CODE = "NORTE";
    private static final String COMPONENT_NAME = "rates-board";

    private final NorteCambiosClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        Document home = Jsoup.parse(client.get(URL));
        String token = home.select("[data-csrf]").attr("data-csrf");
        String snapshot = getSnapshot(home);

        return branches.stream()
                .map(branch -> queryBranch(token, snapshot, branch))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * A branch that fails is skipped, so it doesn't leave the other branches without rates.
     */
    private QueryResponse queryBranch(String token, String snapshot, PlaceBranch branch) {
        try {
            QueryResponse qr = new QueryResponse(branch);
            qr.setDetails(getRates(token, snapshot, branch.getRemoteCode()));
            if (qr.getDetails().isEmpty()) {
                log.warn("Norte Cambios returned no rates for the branch {}", branch.getRemoteCode());
                return null;
            }
            return qr;
        } catch (AppException e) {
            log.warn("Can't query the Norte Cambios branch {}", branch.getRemoteCode(), e);
            return null;
        }
    }

    @Override
    public String getCode() {
        return CODE;
    }

    private static String getSnapshot(Document home) {
        return home.select("[wire:snapshot]")
                .eachAttr("wire:snapshot")
                .stream()
                .filter(s -> s.contains("\"name\":\"" + COMPONENT_NAME + "\""))
                .findFirst()
                .orElseThrow(() -> new AppException(500, "Can't find the Norte Cambios rates component"));
    }

    private List<QueryResponseDetail> getRates(String token, String snapshot, String branchId) {

        try {
            String body = mapper.writeValueAsString(Map.of(
                    "_token", token,
                    "components", List.of(Map.of(
                            "snapshot", snapshot,
                            "updates", Map.of(),
                            "calls", List.of(Map.of(
                                    "path", "",
                                    "method", "setActiveBranch",
                                    "params", List.of(Integer.parseInt(branchId))))))));

            String response = client.postJson(UPDATE_URL, body);
            String html = mapper.readTree(response).path("components").path(0).path("effects").path("html").asString();

            return parseTable(Jsoup.parse(html));
        } catch (JacksonException e) {
            throw new AppException(500, "Invalid response from Norte Cambios (branch: " + branchId + ")", e);
        }
    }

    private static List<QueryResponseDetail> parseTable(Document doc) {

        List<QueryResponseDetail> toRet = new ArrayList<>();
        for (Element row : doc.select("table tbody tr")) {
            Elements columns = row.select("td");
            if (columns.size() != 3) continue;

            // the last span has the short name, eg 'USD', 'BRL•USD' or 'USD CH', we only want plain currencies
            String isoCode = columns.get(0).select("span").last().text().trim();
            if (!isoCode.matches("[A-Z]{3}")) continue;

            // big amounts use '.' as thousand separator (5.660) and small ones ',' as decimal separator (3,40)
            Quote purchase = Quote.parseLocal(isoCode, columns.get(1).select("p").first().ownText());
            Quote sale = Quote.parseLocal(isoCode, columns.get(2).select("p").first().ownText());
            if (purchase.amount().signum() <= 0 || sale.amount().signum() <= 0) continue;

            toRet.add(new QueryResponseDetail(purchase, sale));
        }
        return toRet;
    }
}

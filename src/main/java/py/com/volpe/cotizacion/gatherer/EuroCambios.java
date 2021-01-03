package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.Place.Type;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.net.SocketTimeoutException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 2021-01-02
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class EuroCambios implements Gatherer {

    protected static final String BRANCH_LIST_URL = "https://eurocambios.com.py/v2/index.php/oficinas/";
    protected static final String EXCHANGE_DATA_URL = "https://eurocambios.com.py/v2/sgi/utilsDto.php";

    private final HTTPHelper helper;

    /**
     * EuroCambios has two exchanges, one for the main and another for the secondary branches.
     */
    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        List<QueryResponseDetail> main = getExchangeOf("1");
        List<QueryResponseDetail> secondary = getExchangeOf("2");


        return branches.stream().map(branch -> {

            QueryResponse qr = new QueryResponse(branch);
            if (branch.getName().contains("Sucursal")) {
                qr.setDetails(new ArrayList<>(secondary));
            } else {
                qr.setDetails(new ArrayList<>(main));
            }
            if (qr.getDetails().isEmpty()) return null;
            return qr;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<QueryResponseDetail> getExchangeOf(String id) {

        Map<String, String> params = new HashMap<>();
        params.put("param", "getCotizacionesbySucursal");
        params.put("sucursal", id);
        String page = helper.doPost(EXCHANGE_DATA_URL, params);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = null;
        try {
            data = mapper.readTree(page);
        } catch (JsonProcessingException e) {
            log.warn("Can't parse data of {} of branch {}", getCode(), id, e);
            return Collections.emptyList();
        }
        List<QueryResponseDetail> toRet = new ArrayList<>();

        data.forEach(node -> {
            String currency = mapToISO(node.get("id").asText());
            if (currency == null) return;
            long purchase = Long.parseLong(node.get("compra").asText());
            long sell = Long.parseLong(node.get("venta").asText());
            long lastPurchase = Long.parseLong(node.get("compra_anterior").asText());
            long lastSell = Long.parseLong(node.get("venta_anterior").asText());

            QueryResponseDetail toAdd = new QueryResponseDetail(
                    purchase,
                    sell,
                    currency
            );
            toAdd.setPurchaseTrend(Long.compare(purchase, lastPurchase));
            toAdd.setSaleTrend(Long.compare(sell, lastSell));
            toRet.add(toAdd);
        });
        return toRet;
    }

    private String mapToISO(String id) {
        if (id == null) return null;
        switch (id) {
            case "US":
                return "USD";
            case "EU":
                return "EUR";
            case "PA":
                return "ARG";
            case "RS":
                return "BRL";
            case "PC":
                return "CLP";
            case "PU":
                return "UYU";
            case "YE":
                return "JPY";
            case "DC":
                return "CAD";
            case "FS":
                return "CHF";
            case "LE":
                return "GBP";
            default:
                return null;
        }
    }

    @Override
    public Place build() {

        Place p = Place.builder()
                .name("EuroCambios")
                .code(getCode())
                .type(Type.BUREAU)
                .build();

        try {
            p.setBranches(buildBranches());
        } catch (SocketTimeoutException e) {
            throw new IllegalStateException("Can't build place, request timeout");
        }

        return p;
    }

    private List<PlaceBranch> buildBranches() throws SocketTimeoutException {
        log.info("{} calling {}", getCode(), BRANCH_LIST_URL);

        Document doc = Jsoup.parse(helper.doGet(BRANCH_LIST_URL,
                50_000,
                Collections.singletonMap(HttpHeaders.USER_AGENT, "Chrome"),
                false));

        List<PlaceBranch> data = new ArrayList<>();

        Elements branches = doc.select(".siteorigin-widget-tinymce");

        log.info("Found {} branches", branches.size());


        // the first branch is the 1.
        for (int i = 0; i < branches.size(); i++) {

            Element branch = branches.get(i);
            String title = branch.select("h3").text();

            PlaceBranch pb = new PlaceBranch();
            pb.setName(title);
            pb.setRemoteCode((i + 1) + "");

            Pair<Double, Double> location = findLocation(branch);
            String schedule = findSchedule(branch);
            Optional<String> phoneNumber = findPhone(branch);

            if (location != null) {
                pb.setLatitude(location.getFirst());
                pb.setLongitude(location.getSecond());
            }

            phoneNumber.ifPresent(pb::setPhoneNumber);
            if (schedule != null)
                pb.setSchedule(schedule);

            data.add(pb);
        }

        return data;
    }

    private String findSchedule(Element branch) {
        return branch.select("h4 + p").text();
    }

    private Optional<String> findPhone(Element branch) {
        return branch.select("h3 + p")
                .textNodes()
                .stream()
                .map(TextNode::text)
                .filter(row -> row.trim().startsWith("Tel"))
                .map(row -> row.replace("Tel:", "").trim())
                .findFirst();
    }

    private Pair<Double, Double> findLocation(Element branch) {
        Elements iframe = branch.select("iframe");
        if (iframe == null) return null;
        String href = iframe.attr("src");
        if (href == null) return null;
        return Pair.of(1D, 2D);
    }

    @Override
    public String getCode() {
        return "EURO";
    }
}

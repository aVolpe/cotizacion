package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.Place.Type;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 10/5/18
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class FeCambios implements Gatherer {

    private final HTTPHelper helper;

    private static final String BASE_IMAGE_PATH = "http://www.fecambios.com.py/";
    private static final String BRANCH_DATA_URL = "http://www.fecambios.com.py/sucursales.php";
    private static final String BRANCH_EXCHANGE_DATA_URL = "http://www.fecambios.com.py/inc/getCotizaciones.php";

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        return branches.stream().map(branch -> {

            String data = helper.doPost(BRANCH_EXCHANGE_DATA_URL, Collections.singletonMap("sucu", branch.getRemoteCode()));

            Document root = Jsoup.parse("<html><body>" + data + "</body></html>");
            Elements exchangeDat = root.select(".divcotizacion");

            QueryResponse toRet = new QueryResponse(branch);
            exchangeDat.stream()
                    .map(this::scrapeData)
                    .forEach(toRet::addDetail);

            return toRet;

        }).collect(Collectors.toList());
    }

    private QueryResponseDetail scrapeData(Element e) {

        return new QueryResponseDetail(
                parseAmount(e.select("strong").first().text()),
                parseAmount(e.select("strong").last().text()),
                mapToISO(e.select("h3").first().text()));
    }

    @Override
    public Place build() {


        Place p = Place.builder()
                .type(Type.BUREAU)
                .name("Fe Cambios S.A.")
                .code(getCode())
                .build();

        Document doc = Jsoup.parse(helper.doGet(BRANCH_DATA_URL));

        Element branchesSelect = doc.select("select#sucursales").first();
        Elements branchesData = doc.select(".articlelista");
        List<PlaceBranch> branches = new ArrayList<>(branchesData.size());

        Elements children = branchesSelect.children();
        for (int i = 0; i < children.size(); i++) {
            Element option = children.get(i);
            Element branchData = branchesData.get(i);


            Pair<Double, Double> latlng = extractLatLng(branchData.select("iframe").first().attr("src"));

            PlaceBranch pb = PlaceBranch.builder()
                    .place(p)
                    .remoteCode(option.attr("value"))
                    .name(branchData.select("img").first().attr("alt"))
                    .image(BASE_IMAGE_PATH + branchData.select("img").first().attr("src"))
                    .schedule(branchData.select(".right p:last-child").first().text())
                    .email(branchData.select(".right p:nth-child(4)").first().text())
                    .phoneNumber(branchData.select(".right p:nth-child(3)").first().text())
                    .latitude(latlng.getFirst())
                    .longitude(latlng.getSecond())
                    .build();

            branches.add(pb);

        }

        p.setBranches(branches);
        return p;
    }

    @Override
    public String getCode() {
        return "FeCambios";
    }

    protected Pair<Double, Double> extractLatLng(String gmapsLink) {

        String data = gmapsLink.substring(gmapsLink.indexOf("q=") + 2);

        if (data.contains("&")) {
            data = data.substring(0, data.indexOf('&'));
        }

        String[] parts = data.split(",");

        return Pair.of(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));

    }

    protected Long parseAmount(String amount) {
        return Long.parseLong(amount.substring(0, amount.indexOf(','))
                .replace(".", ""));

    }

    protected static String mapToISO(String key) {
        // We don't care for the check exchange
        switch (key) {
            case "Dolar Efectivo":
                return "USD";
            case "Euro Efectivo":
                return "EUR";
            case "Peso Argentino":
                return "ARS";
            case "Real Efectivo":
                return "BRL";
            case "Yen":
                return "JPY";
            case "Libra Esterlina":
                return "GBP";
            case "Franco Suizo":
                return "CHF";
            case "Dolar Canadiense":
                return "CAD";
            case "Peso Chileno":
                return "CLP";
            case "Peso Uruguayo":
                return "UYU";
            default:
                log.warn("Can't map {} to iso code", key);
                return null;
        }
    }

}

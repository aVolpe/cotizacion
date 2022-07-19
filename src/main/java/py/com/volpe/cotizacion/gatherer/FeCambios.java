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
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Arturo Volpe
 * @since 10/5/18
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class FeCambios implements Gatherer {

    private final HTTPHelper helper;

    private static final String BRANCH_EXCHANGE_DATA_URL = "https://www.fecambios.com.py/inc/getCotizaciones.php";

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        return branches.stream().map(branch -> {

            try {
                String data = helper.doPost(BRANCH_EXCHANGE_DATA_URL, Collections.singletonMap("sucu", branch.getRemoteCode()));

                Document root = Jsoup.parse("<html><body>" + data + "</body></html>");
                Elements exchangeDat = root.select(".divcotizacion");

                QueryResponse toRet = new QueryResponse(branch);
                exchangeDat.stream()
                        .map(this::scrapeData)
                        .forEach(toRet::addDetail);

                return toRet;
            } catch (Exception e) {
                log.warn("Can't get data from branch {}", branch.getRemoteCode(), e);
                return null;
            }

        })
                .filter(Objects::nonNull)
                .toList();
    }

    private QueryResponseDetail scrapeData(Element e) {

        return new QueryResponseDetail(
                parseAmount(e.select("strong").first().text()),
                parseAmount(e.select("strong").last().text()),
                mapToISO(e.select("h3").first().text()));
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

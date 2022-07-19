package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.Collections;
import java.util.List;

/**
 * @author Arturo Volpe
 * @since 9/21/18
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class VisionBanco implements Gatherer {


    private static final String EXCHANGE_URL = "https://www.visionbanco.com/";
    private final HTTPHelper helper;

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        QueryResponse qr = new QueryResponse(place);

        String html = helper.doGet(EXCHANGE_URL);

        Document doc = Jsoup.parse(html);

        Element tables = doc.getElementById("efectivo");
        if (tables == null) {
            log.warn("No tables found in vision, no data can be parsed");
            return Collections.emptyList();
        }
        Elements exchanges = tables.select("tr");


        for (Element e : exchanges) {
            Elements columns = e.select("td");
            qr.addDetail(new QueryResponseDetail(
                    parse(columns.get(1).text()),
                    parse(columns.get(2).text()),
                    getISOName(columns.get(0).text().trim())
            ));
        }

        return Collections.singletonList(qr);

    }

    private static String getISOName(String name) {
        return switch (name) {
            case "DOLARES" -> "USD";
            case "REAL BRASILEÑO" -> "BRL";
            case "PESO ARGENTINO" -> "ARS";
            case "EUROS" -> "EUR";
            default -> null;
        };

    }

    private static Long parse(String s) {
        return Long.parseLong(s
                .replace("compra", "")
                .replace("venta", "")
                .trim()
                .replaceAll("\\.", ""));
    }

    @Override
    public String getCode() {
        return "VISION_BANCO";
    }
}

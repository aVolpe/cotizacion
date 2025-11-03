package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.io.IOException;
import java.util.*;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class Amambay implements Gatherer {

    private static final String URL_CHANGE = "https://www.bancobasa.com.py/";
    private static final String CODE = "AMAMBAY";

    private final HTTPHelper httpHelper;

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public List<QueryResponse> doQuery(Place p, List<PlaceBranch> branches) {

        try {

            QueryResponse qr = new QueryResponse(p);

            String html = httpHelper.doGet(URL_CHANGE, 5000, Map.of(), true);

            Document doc = Jsoup.parse(html);
            // The page contains .cta-buttons with multiple <a> entries, each having an <img> and a <p>
            Elements anchors = doc.select(".cta-buttons a");

            for (Element a : anchors) {
                Optional<QueryResponseDetail> detail = parseAnchor(a);
                detail.ifPresent(qr::addDetail);
            }

            return Collections.singletonList(qr);

        } catch (IOException e) {
            throw new AppException(500, "cant read response from Amambay ", e);
        }

    }

    private Optional<QueryResponseDetail> parseAnchor(Element a) {
        try {
            Element img = a.selectFirst(".cta-icon img");
            if (img == null) return Optional.empty();

            String src = img.attr("src");
            String alt = img.attr("alt");
            Element pElem = a.selectFirst("p");
            String ptext = pElem != null ? pElem.text() : null;

            String data;
            if (!alt.isBlank()) data = alt;
            else if (ptext != null && !ptext.isBlank()) data = ptext;
            else data = "";

            String filename = src.substring(src.lastIndexOf('/') + 1).toLowerCase(Locale.ROOT);
            String iso = mapImageToISO(filename);
            if (iso == null) {
                log.warn("Unknown currency image {} for Amambay", filename);
                return Optional.empty();
            }

            // data expected like: "Compra 6.940 | Venta 7.210"
            String[] parts = data.split("\\|");
            if (parts.length < 2) return Optional.empty();

            long purchase = parseAmount(parts[0]);
            long sale = parseAmount(parts[1]);

            if (purchase <= 0 || sale <= 0) return Optional.empty();

            return Optional.of(new QueryResponseDetail(purchase, sale, iso));
        } catch (Exception e) {
            log.warn("Error parsing an entry from Amambay page", e);
            return Optional.empty();
        }
    }

    private String mapImageToISO(String filename) {
        if (filename.contains("dolar")) return "USD";
        if (filename.contains("dolarblue")) return "USD";
        if (filename.contains("euro")) return "EUR";
        if (filename.contains("reales") || filename.contains("real")) return "BRL";
        if (filename.contains("pesosargentinos") || filename.contains("peso") || filename.contains("argentinos")) return "ARS";
        if (filename.contains("libra")) return "GBP";
        if (filename.contains("yen")) return "JPY";
        // add more mappings if needed
        return null;
    }

    private long parseAmount(String part) {
        // extract digits from the part. Example: "Compra 6.940 " -> "6940"
        try {
            String digits = part.replaceAll("\\D", "");
            if (digits.isBlank()) return 0L;
            return Long.parseLong(digits);
        } catch (Exception e) {
            log.warn("Can't parse amount from '{}'", part, e);
            return 0L;
        }
    }

}

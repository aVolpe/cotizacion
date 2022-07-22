package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Arturo Volpe
 * @since 2022-07-19
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class Mercosur implements Gatherer {

    private static final String BASE_URL = "https://2019.mercosurcambios.com/Sucursale/%s";

    private final HTTPHelper helper;

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        return branches
                .stream()
                .map(branch -> {
                    QueryResponse qr = new QueryResponse(branch);
                    qr.setDetails(getExchangeOf(branch.getRemoteCode()));
                    if (qr.getDetails().isEmpty()) return null;
                    return qr;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<QueryResponseDetail> getExchangeOf(String id) {

        String html = helper.doGet(BASE_URL.formatted(id));

        Document doc = Jsoup.parse(html, "", Parser.htmlParser());
        List<QueryResponseDetail> toRet = new ArrayList<>();

        for (Element e : doc.select("table.table tbody tr")) {
            QueryResponseDetail ed = new QueryResponseDetail();
            Elements columns = e.getElementsByTag("th");

            if (columns.size() != 5) continue;

            String currency = getCurrency(columns.get(1).text());
            if (currency == null) {
                continue;
            }

            ed.setIsoCode(currency);
            ed.setPurchasePrice(parseNumber(columns.get(3).text()));
            ed.setSalePrice(parseNumber(columns.get(4).text()));

            toRet.add(ed);
        }
        return toRet;
    }

    private long parseNumber(String text) {
        if (text == null || text.isEmpty()) return 0L;
        return Long.parseLong(text);
    }

    protected String getCurrency(String baseText) {
        if (baseText == null || baseText.isEmpty()) return null;
        String normalized = Normalizer.normalize(baseText, Normalizer.Form.NFD).toLowerCase(Locale.ROOT);

        if (!normalized.toLowerCase(Locale.ROOT).endsWith("guarani")) return null; // we don't support other exchanges

        String countryName = normalized.substring(0, normalized.indexOf(' '));
        // remote not ascii digits
        return switch (countryName) {
            case "dólar", "dolar" -> "USD";
            case "real" -> "BRL";
            case "peso" -> "ARS";
            case "euro" -> "EUR";
            default -> null;
        };
    }

    @Override
    public String getCode() {
        return "MERCOSUR";
    }
}

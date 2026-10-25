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
import py.com.volpe.cotizacion.domain.Quote;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Mundial Cambios shows every branch in the home page, each one in a tab (Elementor nested tabs).
 * <p>
 * The tab title ('Globo Center') controls a panel ({@code aria-controls="e-n-tab-content-<id>"}) with two tables,
 * 'Cambio del día' (the rates against the guarani) and 'Cambio de arbitraje' (cross rates, ignored). The remote
 * code of the branch is the normalized tab title ('globo-center').
 *
 * @author Arturo Volpe
 * @since 2021-01-02
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class Mundial implements Gatherer {

    static final String URL = "https://mundialcambios.com.py/";

    private static final String DAILY_RATES_TITLE = "CAMBIO DEL DIA";

    /**
     * The site uses the name of the currency, the others (DOLAR CHQ. LOCAL, GUARANI) are ignored.
     */
    private static final Map<String, String> ISO_CODES = Map.of(
            "DOLARES AMERICANOS", "USD",
            "REALES", "BRL",
            "PESOS ARGENTINOS", "ARS",
            "EUROS", "EUR",
            "YEN JAPONES", "JPY",
            "LIBRAS ESTERLINAS", "GBP",
            "FRANCO SUIZO", "CHF",
            "DOLAR CANADIENSE", "CAD",
            "PESO CHILENO", "CLP",
            "PESO URUGUAYO", "UYU");

    private final HTTPHelper helper;

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        Map<String, List<QueryResponseDetail>> rates = parse(Jsoup.parse(helper.doGet(URL)));

        return branches.stream()
                .map(branch -> {
                    List<QueryResponseDetail> details = rates.remove(branch.getRemoteCode());
                    if (details == null) {
                        log.warn("Mundial Cambios has no rates for the branch {}", branch.getRemoteCode());
                        return null;
                    }
                    QueryResponse qr = new QueryResponse(branch);
                    qr.setDetails(details);
                    return qr;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * The rates of each branch, by remote code. The tabs without rates (the branch locator also uses tabs) are
     * skipped.
     */
    static Map<String, List<QueryResponseDetail>> parse(Document doc) {

        Map<String, List<QueryResponseDetail>> toRet = new LinkedHashMap<>();
        for (Element tab : doc.select(".e-n-tab-title[aria-controls]")) {
            Element panel = doc.getElementById(tab.attr("aria-controls"));
            if (panel == null) continue;

            List<QueryResponseDetail> details = parseDailyRates(panel);
            if (details.isEmpty()) continue;

            toRet.put(toRemoteCode(tab.text()), details);
        }
        if (toRet.isEmpty()) log.warn("Mundial Cambios returned a page without rates");
        return toRet;
    }

    /**
     * The table is in the same container than its title, the panel also has the 'Cambio de arbitraje' one.
     */
    private static List<QueryResponseDetail> parseDailyRates(Element panel) {

        List<QueryResponseDetail> toRet = new ArrayList<>();
        for (Element title : panel.select(".elementor-heading-title")) {
            if (!normalize(title.text()).equals(DAILY_RATES_TITLE)) continue;

            Element container = title.closest(".e-con");
            if (container == null) continue;

            for (Element row : container.select("table.tabla-cotizaciones tbody tr")) {
                Elements columns = row.select("td");
                if (columns.size() != 3) continue;

                String isoCode = ISO_CODES.get(normalize(columns.get(0).text()));
                if (isoCode == null) continue;

                // big amounts use '.' as thousand separator (5.710,00) and ',' as decimal separator (3,55)
                Quote purchase = Quote.parseLocal(isoCode, columns.get(1).text());
                Quote sale = Quote.parseLocal(isoCode, columns.get(2).text());
                if (purchase.amount().signum() <= 0 || sale.amount().signum() <= 0) continue;

                toRet.add(new QueryResponseDetail(purchase, sale));
            }
        }
        return toRet;
    }

    /**
     * 'Salto del Guairá' -> 'salto-del-guaira'
     */
    static String toRemoteCode(String tabTitle) {
        return normalize(tabTitle).toLowerCase(Locale.ROOT).replace(' ', '-');
    }

    /**
     * Upper case, without accents and with single spaces: ' Cambio del  día ' -> 'CAMBIO DEL DIA'.
     */
    private static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[\\s\\u00A0]+", " ")
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    @Override
    public String getCode() {
        return "MUNDIAL";
    }
}

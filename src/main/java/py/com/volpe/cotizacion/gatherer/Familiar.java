package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.util.Collections;
import java.util.List;

/**
 * Banco Familiar, the rates are a Webflow CMS list rendered in the page (one card per currency, with its
 * 'Compra' and 'Venta'), the same rates for every branch.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class Familiar implements Gatherer {

    private static final String EXCHANGE_URL = "https://www.familiar.com.py/cambios";
    private final HTTPHelper helper;

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        QueryResponse qr = new QueryResponse(place);

        Elements cards = Jsoup.parse(helper.doGet(EXCHANGE_URL)).select(".collection-list.grid-4x1 .collection-item");
        if (cards.isEmpty()) {
            log.warn("No exchange cards found in familiar, no data can be parsed");
            return Collections.emptyList();
        }

        for (Element card : cards) {
            String iso = getISOName(card.select(".heading-s").text().trim());
            if (iso == null) continue;
            // the values are the even '.text-xl': 'Compra', <purchase>, 'Venta', <sale>
            Elements values = card.select(".text-xl");
            if (values.size() != 4) {
                log.warn("Familiar card of {} has {} values, it is skipped", iso, values.size());
                continue;
            }
            qr.addDetail(new QueryResponseDetail(
                    Quote.parseLocal(iso, values.get(1).text()),
                    Quote.parseLocal(iso, values.get(3).text())
            ));
        }

        return Collections.singletonList(qr);
    }

    private static String getISOName(String name) {
        // We don't care for the 'Cheque / Transf.' rates, only the cash ones
        return switch (name) {
            case "Dólar Americano Efectivo" -> "USD";
            case "Euro Efectivo" -> "EUR";
            case "Real" -> "BRL";
            // The ARS is not gathered, the site publishes values that look unreliable (1 / 5 in 2026-10)
            case "Peso" -> null;
            default -> null;
        };
    }

    @Override
    public String getCode() {
        return "FAMILIAR";
    }
}

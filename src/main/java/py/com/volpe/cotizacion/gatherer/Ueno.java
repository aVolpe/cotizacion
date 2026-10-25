package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ueno bank shows its rates in cards, one per currency and modality
 * ({@code <div class="divisas-card" data-currency="USD" data-subtitle="en efectivo">}) with the purchase and sale
 * prices ('Gs. 5.650').
 * <p>
 * Each modality (app, cash and transfers) has different rates. We publish only the cash ('en efectivo') rates, at
 * the bank level (without branches), as they are the ones comparable with the exchange houses; the app and transfer
 * cards are ignored. The page refreshes the cards with an ajax call that needs a nonce of the page, so we read the
 * cards rendered in the page instead.
 *
 * @author Arturo Volpe
 * @since 2026-10-25
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class Ueno implements Gatherer {

    static final String URL = "https://www.ueno.com.py/cambio-de-moneda/";

    static final Map<String, String> HEADERS = Map.of(
            HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36",
            HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            HttpHeaders.ACCEPT_LANGUAGE, "es-PY,es;q=0.9");

    private static final int TIMEOUT_MS = 30_000;

    private static final String CASH = "en efectivo";

    /**
     * The other modalities of the site, expected and ignored.
     */
    private static final Set<String> IGNORED_MODALITIES = Set.of("en tu app", "en transferencias");

    private static final Pattern AMOUNT = Pattern.compile("\\d[\\d.,]*");

    private final HTTPHelper helper;

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        QueryResponse qr = new QueryResponse(place);
        qr.setDetails(parse(Jsoup.parse(fetch())));
        return Collections.singletonList(qr);
    }

    String fetch() {
        try {
            return helper.doGet(URL, TIMEOUT_MS, HEADERS, false);
        } catch (SocketTimeoutException e) {
            throw new AppException(500, "Timeout querying ueno", e);
        }
    }

    /**
     * The cash rates of the page. The cards of other modalities or without a valid currency or price are skipped.
     */
    static List<QueryResponseDetail> parse(Document doc) {

        List<QueryResponseDetail> toRet = new ArrayList<>();
        for (Element card : doc.select(".divisas-card[data-currency][data-subtitle]")) {
            String isoCode = card.attr("data-currency").trim();
            String subtitle = card.attr("data-subtitle").trim().toLowerCase(Locale.ROOT);

            if (!isoCode.matches("[A-Z]{3}")) {
                log.warn("ueno card without a valid currency: '{}' ({})", isoCode, subtitle);
                continue;
            }
            if (!subtitle.equals(CASH)) {
                if (IGNORED_MODALITIES.contains(subtitle)) log.debug("ueno card ignored: {} {}", isoCode, subtitle);
                else log.warn("ueno card with an unknown modality: '{}' ({})", subtitle, isoCode);
                continue;
            }

            QueryResponseDetail detail = parseCard(card, isoCode);
            if (detail == null) {
                log.warn("ueno card without prices: {} {}", isoCode, subtitle);
                continue;
            }
            toRet.add(detail);
        }
        if (toRet.isEmpty()) log.warn("ueno returned a page without cash rates");
        return toRet;
    }

    /**
     * Each price is a row with two spans, the label ('Compra' or 'Venta') and the amount ('Gs. 5.680').
     */
    private static QueryResponseDetail parseCard(Element card, String isoCode) {

        Quote purchase = null;
        Quote sale = null;
        for (Element row : card.select("div:has(> span + span)")) {
            Elements spans = row.select("> span");
            String label = spans.first().text().trim().toLowerCase(Locale.ROOT);
            Quote quote = parseAmount(isoCode, spans.last().text());
            if (label.equals("compra")) purchase = quote;
            else if (label.equals("venta")) sale = quote;
        }
        if (purchase == null || sale == null) return null;
        if (purchase.amount().signum() <= 0 || sale.amount().signum() <= 0) return null;
        return new QueryResponseDetail(purchase, sale);
    }

    /**
     * 'Gs. 5.680' -> 5680
     */
    private static Quote parseAmount(String isoCode, String text) {
        Matcher matcher = AMOUNT.matcher(text.replace("Gs.", ""));
        if (!matcher.find()) return null;
        return Quote.parseLocal(isoCode, matcher.group());
    }

    @Override
    public String getCode() {
        return "UENO";
    }
}

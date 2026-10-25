package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Arturo Volpe
 * @since 2026-10-25
 */
class UenoTest {

    /**
     * The cash cards of the fixture, the app (USD) and transfer (EUR) cards are not here.
     */
    private static final List<QueryResponseDetail> CASH_RATES = List.of(
            new QueryResponseDetail(Quote.of("USD", 5650), Quote.of("USD", 5860)),
            new QueryResponseDetail(Quote.of("EUR", 6250), Quote.of("EUR", 6850)));

    /**
     * A copy of the selector of the script that refreshes the cards (outside the script), a card of an unknown
     * currency, one of an unknown modality, one of the app and a valid cash one.
     */
    private static final String INVALID_CARDS = """
            <div class="divisas-card" data-card-index="0" data-currency="${cardData.currency}" data-subtitle="${cardData.subtitle}">
              <div class="p-5"><div><span>Compra</span><span>Gs. 1</span></div><div><span>Venta</span><span>Gs. 2</span></div></div>
            </div>
            <div class="divisas-card" data-card-index="1" data-currency="Dólar" data-subtitle="en efectivo">
              <div class="p-5"><div><span>Compra</span><span>Gs. 5.000</span></div><div><span>Venta</span><span>Gs. 5.100</span></div></div>
            </div>
            <div class="divisas-card" data-card-index="2" data-currency="USD" data-subtitle="en cripto">
              <div class="p-5"><div><span>Compra</span><span>Gs. 5.000</span></div><div><span>Venta</span><span>Gs. 5.100</span></div></div>
            </div>
            <div class="divisas-card" data-card-index="3" data-currency="USD" data-subtitle="en tu app">
              <div class="p-5"><div><span>Compra</span><span>Gs. 5.000</span></div><div><span>Venta</span><span>Gs. 5.100</span></div></div>
            </div>
            <div class="divisas-card" data-card-index="4" data-currency="BRL" data-subtitle="en efectivo">
              <div class="p-5"><div><span>Compra</span><span>Gs. 1.050</span></div><div><span>Venta</span><span>Gs. 1.150</span></div></div>
            </div>
            """;

    @Test
    void readsOnlyTheCashCardsOfTheSite() {
        assertEquals(CASH_RATES, Ueno.parse(Jsoup.parse(page("ueno.html"))));
    }

    @Test
    void returnsASingleResponseOfTheBankWithoutBranch() {
        Place place = new Place("ueno bank", "UENO");

        List<QueryResponse> result = new Ueno(helperReturning(page("ueno.html"))).doQuery(place, List.of());

        assertEquals(1, result.size());
        assertSame(place, result.get(0).getPlace());
        assertNull(result.get(0).getBranch());
        assertEquals(CASH_RATES, result.get(0).getDetails());
    }

    @Test
    void ignoresTheAppAndTransferCards() {
        List<QueryResponseDetail> rates = Ueno.parse(Jsoup.parse(page("ueno.html")));

        // USD 5.680 / 5.850 is the app card, EUR 6.250 / 6.700 the transfer one
        assertFalse(rates.stream().anyMatch(d -> d.getPurchase().equals(Quote.of("USD", 5680))));
        assertFalse(rates.stream().anyMatch(d -> d.getSale().equals(Quote.of("EUR", 6700))));
    }

    @Test
    void skipsTheCardsWithoutIsoCodeOrOfAnotherModality() {
        assertEquals(List.of(new QueryResponseDetail(Quote.of("BRL", 1050), Quote.of("BRL", 1150))),
                Ueno.parse(Jsoup.parse(INVALID_CARDS)));
    }

    @Test
    void queriesTheSiteWithBrowserHeaders() throws IOException {
        HTTPHelper helper = Mockito.mock(HTTPHelper.class);
        Mockito.when(helper.doGet(Mockito.eq(Ueno.URL), Mockito.anyInt(), Mockito.eq(Ueno.HEADERS), Mockito.eq(false)))
                .thenReturn(page("ueno.html"));

        Place place = new Place("ueno bank", "UENO");

        assertEquals(CASH_RATES, new Ueno(helper).doQuery(place, List.of()).get(0).getDetails());
    }

    /**
     * One real GET to the site with the {@link HTTPHelper}, to check that it passes Cloudflare.
     */
    @Test
    @Disabled("Queries the real site, run it by hand")
    void liveQueryPassesCloudflare() {
        List<QueryResponseDetail> rates = Ueno.parse(Jsoup.parse(new Ueno(new HTTPHelper()).fetch()));
        System.out.println("UENO LIVE: " + rates);
        assertEquals(2, rates.size());
    }

    private static HTTPHelper helperReturning(String page) {
        HTTPHelper helper = Mockito.mock(HTTPHelper.class);
        try {
            Mockito.when(helper.doGet(Mockito.eq(Ueno.URL), Mockito.anyInt(), Mockito.anyMap(), Mockito.anyBoolean()))
                    .thenReturn(page);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return helper;
    }

    private static String page(String resource) {
        try {
            return IOUtils.toString(UenoTest.class.getResourceAsStream(resource), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

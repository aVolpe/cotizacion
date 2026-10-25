package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arturo Volpe
 * @since 2026-10-25
 */
class LaMonedaTest {

    @Test
    void readsTheGuaraniRatesOfTheMainBranch() {
        List<QueryResponse> result = tester().withTheBranch("casa_matriz", "lamoneda_matriz.json")
                .withBranches("casa_matriz")
                .run();

        // the cross rates (DOLAR/REAL, DOLAR/PESOS, DOLAR/EURO) and the DOLAR INUSUAL are ignored
        List<QueryResponseDetail> details = result.get(0).getDetails();
        assertEquals(4, details.size());
        assertDetail(details.get(0), Quote.of("USD", 5700), Quote.of("USD", 5770));
        assertDetail(details.get(1), Quote.of("BRL", 1130), Quote.of("BRL", 1165));
        assertDetail(details.get(2), Quote.of("ARS", "3.5"), Quote.of("ARS", "3.65"));
        assertDetail(details.get(3), Quote.of("EUR", 6200), Quote.of("EUR", 6650));
    }

    @Test
    void quotesEachBranchWithItsOwnRates() {
        tester().withTheBranch("casa_matriz", "lamoneda_matriz.json")
                .withTheBranch("sucursal_jebai", "lamoneda_jebai.json")
                .withBranches("casa_matriz", "sucursal_jebai")
                .query()
                .shouldReturnBranches("casa_matriz", "sucursal_jebai")
                .shouldHaveOnlyCurrencies("USD", "BRL", "ARS", "EUR")
                .shouldQuote("sucursal_jebai", "USD", "5.650", "5.750")
                .shouldQuote("sucursal_jebai", "BRL", "1.120", "1.165")
                .shouldQuote("sucursal_jebai", "ARS", "3,5", "3,8")
                .shouldQuote("sucursal_jebai", "EUR", "6.225", "6.740");
    }

    @Test
    void ignoresTheCurrenciesWithoutRateOrUnknown() {
        tester().withTheResponse("casa_matriz", """
                        {"success":true,"cotizaciones":[
                         {"moneda1":"DOLAR","moneda2":"GUARANI","compra":5700,"venta":5770},
                         {"moneda1":"REAL","moneda2":"GUARANI","compra":0,"venta":0},
                         {"moneda1":"EURO","moneda2":"GUARANI","compra":6200,"venta":0},
                         {"moneda1":"PESOS","moneda2":"GUARANI","compra":"3,5","venta":"3,65"},
                         {"moneda1":"YEN","moneda2":"GUARANI","compra":40,"venta":45}]}""")
                .withBranches("casa_matriz")
                .query()
                .shouldHaveOnlyCurrencies("USD");
    }

    /**
     * As a double 3.12499999999999999999 is 3.125, that would be rounded to 3.13.
     */
    @Test
    void readsThePricesWithoutLosingPrecision() {
        tester().withTheResponse("casa_matriz", """
                        {"success":true,"cotizaciones":[
                         {"moneda1":"PESOS","moneda2":"GUARANI","compra":3.12499999999999999999,"venta":3.65}]}""")
                .withBranches("casa_matriz")
                .query()
                .shouldQuote("casa_matriz", "ARS", "3,12", "3,65");
    }

    @Test
    void skipsTheBranchesWithoutRatesOrThatFail() {
        tester().withTheBranch("casa_matriz", "lamoneda_matriz.json")
                .withTheResponse("sucursal_centro", "{\"success\":false,\"cotizaciones\":[]}")
                .withTheResponse("sucursal_km7", "{\"success\":true,\"cotizaciones\":[]}")
                .withTheResponse("sucursal_algesa", "<html>not json</html>")
                .withAFailingBranch("sucursal_jebai")
                .withBranches("sucursal_centro", "sucursal_km7", "sucursal_algesa", "sucursal_jebai", "casa_matriz")
                .query()
                .shouldReturnBranches("casa_matriz");
    }

    @Test
    void ignoresTheRatesIfTheResponseIsNotSuccessful() {
        String body = """
                {"success":false,"cotizaciones":[{"moneda1":"DOLAR","moneda2":"GUARANI","compra":5700,"venta":5770}]}""";
        assertEquals(List.of(), tester().withTheResponse("casa_matriz", body)
                .withBranches("casa_matriz")
                .run());
    }

    @Test
    void queriesTheApiWithTheRemoteCode() {
        assertEquals("https://www.lamoneda.com.py/api/cotizaciones.php?sucursal=sucursal_jebai", LaMoneda.getBranchUrl("sucursal_jebai"));
    }

    private static void assertDetail(QueryResponseDetail detail, Quote purchase, Quote sale) {
        assertEquals(purchase, detail.getPurchase());
        assertEquals(sale, detail.getSale());
    }

    private static Tester tester() {
        return new Tester();
    }

    private static final class Tester {

        private final Map<String, String> responses = new HashMap<>();
        private final Set<String> failing = new HashSet<>();
        private final HTTPHelper helper = Mockito.mock(HTTPHelper.class);
        private List<String> branches = List.of();

        Tester withTheBranch(String remoteCode, String resource) {
            try {
                return withTheResponse(remoteCode,
                        IOUtils.toString(LaMonedaTest.class.getResourceAsStream(resource), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        Tester withTheResponse(String remoteCode, String body) {
            responses.put(remoteCode, body);
            failing.remove(remoteCode);
            return this;
        }

        /**
         * The site answers this branch with a 500.
         */
        Tester withAFailingBranch(String remoteCode) {
            responses.remove(remoteCode);
            failing.add(remoteCode);
            return this;
        }

        /**
         * The branches stored in the database, by remote code.
         */
        Tester withBranches(String... remoteCodes) {
            branches = List.of(remoteCodes);
            return this;
        }

        List<QueryResponse> run() {
            try {
                for (Map.Entry<String, String> response : responses.entrySet()) {
                    Mockito.when(helper.doGet(ArgumentMatchers.eq(LaMoneda.getBranchUrl(response.getKey())),
                                    ArgumentMatchers.anyInt(), ArgumentMatchers.anyMap(), ArgumentMatchers.anyBoolean()))
                            .thenReturn(response.getValue());
                }
                for (String code : failing) {
                    Mockito.when(helper.doGet(ArgumentMatchers.eq(LaMoneda.getBranchUrl(code)),
                                    ArgumentMatchers.anyInt(), ArgumentMatchers.anyMap(), ArgumentMatchers.anyBoolean()))
                            .thenThrow(new AppException(500, "Internal error"));
                }
            } catch (SocketTimeoutException e) {
                throw new IllegalStateException(e);
            }

            Place place = new Place("La Moneda Cambios", "LAMONEDA");
            place.setBranches(branches.stream().map(code -> new PlaceBranch(0, code)).toList());

            return new LaMoneda(helper).doQuery(place, place.getBranches());
        }

        QueryAsserter query() {
            return new QueryAsserter(run());
        }
    }
}

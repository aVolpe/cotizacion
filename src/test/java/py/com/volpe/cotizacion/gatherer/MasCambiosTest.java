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
class MasCambiosTest {

    @Test
    void readsTheCurrenciesOfTheSiteFromTheMainBranch() {
        List<QueryResponse> result = tester().withTheBranch("matriz", "mascambios_matriz.json")
                .withBranches("matriz")
                .run();

        // the 3 (guaraní, 0/0) and the unknown ids (18, 19) are ignored
        List<QueryResponseDetail> details = result.get(0).getDetails();
        assertEquals(4, details.size());
        assertDetail(details.get(0), Quote.of("USD", 5850), Quote.of("USD", 5950));
        assertDetail(details.get(1), Quote.of("BRL", 1120), Quote.of("BRL", 1155));
        assertDetail(details.get(2), Quote.of("ARS", "3.6"), Quote.of("ARS", "3.85"));
        assertDetail(details.get(3), Quote.of("EUR", 6790), Quote.of("EUR", 7050));
    }

    @Test
    void ignoresTheUnknownCurrencies() {
        tester().withTheBranch("sucursal1", "mascambios_asu.json")
                .withBranches("sucursal1")
                .query()
                .shouldHaveOnlyCurrencies("USD", "BRL", "ARS", "EUR")
                .shouldQuote("sucursal1", "USD", "5.670", "5.770")
                .shouldQuote("sucursal1", "BRL", "1.110", "1.170")
                .shouldQuote("sucursal1", "ARS", "3,4", "3,9")
                .shouldQuote("sucursal1", "EUR", "6.250", "6.550");
    }

    @Test
    void ignoresTheCurrenciesWithoutRate() {
        tester().withTheResponse("matriz", """
                        {"cotizaciones":{"1":{"compra":"5850","venta":"5950"},"2":{"compra":"0","venta":"0"},
                         "4":{"compra":"3.6","venta":"0"},"5":{"compra":"abc","venta":"7050"}},"fecha":"2026-10-25 16:30:06"}""")
                .withBranches("matriz")
                .query()
                .shouldHaveOnlyCurrencies("USD");
    }

    @Test
    void skipsTheBranchesWithoutRatesOrThatFail() {
        tester().withTheBranch("matriz", "mascambios_matriz.json")
                .withTheResponse("sucursal2", "{\"cotizaciones\":{},\"fecha\":\"2026-10-25 16:30:06\"}")
                .withTheResponse("sucursal3", "<html>not json</html>")
                .withAFailingBranch("sucursal4")
                .withTheBranch("sucursal1", "mascambios_asu.json")
                .withBranches("matriz", "sucursal2", "sucursal3", "sucursal4", "sucursal1")
                .query()
                .shouldReturnBranches("matriz", "sucursal1");
    }

    @Test
    void queriesTheApiWithTheRemoteCode() {
        assertEquals("https://mascambios.com.py/api/cotizaciones.php?sucursal=sucursal1", MasCambios.getBranchUrl("sucursal1"));
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
                        IOUtils.toString(MasCambiosTest.class.getResourceAsStream(resource), StandardCharsets.UTF_8));
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
                    Mockito.when(helper.doGet(ArgumentMatchers.eq(MasCambios.getBranchUrl(response.getKey())),
                                    ArgumentMatchers.anyInt(), ArgumentMatchers.anyMap(), ArgumentMatchers.anyBoolean()))
                            .thenReturn(response.getValue());
                }
                for (String code : failing) {
                    Mockito.when(helper.doGet(ArgumentMatchers.eq(MasCambios.getBranchUrl(code)),
                                    ArgumentMatchers.anyInt(), ArgumentMatchers.anyMap(), ArgumentMatchers.anyBoolean()))
                            .thenThrow(new AppException(500, "Internal error"));
                }
            } catch (SocketTimeoutException e) {
                throw new IllegalStateException(e);
            }

            Place place = new Place("Más Cambios", "MASCAMBIOS");
            place.setBranches(branches.stream().map(code -> new PlaceBranch(0, code)).toList());

            return new MasCambios(helper).doQuery(place, place.getBranches());
        }

        QueryAsserter query() {
            return new QueryAsserter(run());
        }
    }
}

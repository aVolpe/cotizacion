package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arturo Volpe
 * @since 10/5/18
 */
class FeCambiosTest {

    private static final String[] SITE_BRANCHES = {"pal", "vmo", "cde", "slo"};

    @Test
    void returnsEveryBranchWithRates() {
        tester().withTheSite()
                .withBranches("pal", "vmo", "cde", "slo")
                .query()
                .shouldReturnBranches("pal", "vmo", "cde", "slo");
    }

    @Test
    void readsTheFourCurrenciesOfEachBranch() {
        tester().withTheSite()
                .withBranches(SITE_BRANCHES)
                .query()
                .shouldHaveOnlyCurrencies("USD", "BRL", "EUR", "ARS");
    }

    @Test
    void quotesEachBranchWithItsOwnRates() {
        tester().withTheSite()
                .withBranches(SITE_BRANCHES)
                .query()
                .shouldQuote("pal", "USD", "5.600", "5.780")
                .shouldQuote("pal", "BRL", "1.100", "1.250")
                .shouldQuote("vmo", "EUR", "6.350", "6.900")
                .shouldQuote("vmo", "ARS", "3", "4,20")
                .shouldQuote("cde", "BRL", "1.105", "1.150")
                .shouldQuote("cde", "ARS", "3,45", "3,70")
                .shouldQuote("slo", "USD", "5.650", "5.780")
                .shouldQuote("slo", "EUR", "6.250", "7.000");
    }

    @Test
    void keepsTheOddButValidRates() {
        tester().withTheSite()
                .withBranches("pal")
                .query()
                .shouldQuote("pal", "ARS", "3", "9");
    }

    @Test
    void ignoresTheCurrenciesWithoutRate() {
        tester().withTheResponse("pal", """
                        [{"name":"5600","description":"5780","id":"USD"},
                         {"name":"0","description":"0","id":"BRL"},
                         {"name":"6200","description":"0","id":"EUR"},
                         {"name":"","description":"","id":"ARS"},
                         {"name":"abc","description":"7","id":"CLP"}]""")
                .withBranches("pal")
                .query()
                .shouldHaveOnlyCurrencies("USD")
                .shouldQuote("pal", "USD", "5.600", "5.780")
                .shouldNotQuote("pal", "BRL")
                .shouldNotQuote("pal", "EUR")
                .shouldNotQuote("pal", "ARS")
                .shouldNotQuote("pal", "CLP");
    }

    @Test
    void skipsTheBranchesWithoutRates() {
        tester().withTheSite()
                .withBranches("pjc", "cde")
                .query()
                .shouldReturnBranches("cde");
    }

    @Test
    void skipsTheBranchesThatFail() {
        tester().withTheSite()
                .withAFailingBranch("pal")
                .withTheResponse("vmo", "<html>not json</html>")
                .withBranches("pal", "vmo", "slo")
                .query()
                .shouldReturnBranches("slo");
    }

    /**
     * The inactive branches (as Pedro Juan Caballero) are filtered by {@link Place#getBranches()}, see
     * BranchMigrationsTest, the gatherer must query only the ones it receives.
     */
    @Test
    void queriesOnlyTheReceivedBranches() {
        tester().withTheSite()
                .withBranches("cde")
                .requests()
                .shouldBe("https://www.fecambios.com.py/cde-tasks-list-cot.php");
    }

    private static Tester tester() {
        return new Tester();
    }

    private static final class Tester {

        private final Map<String, String> responses = new HashMap<>();
        private final Set<String> failing = new HashSet<>();
        private final HTTPHelper helper = Mockito.mock(HTTPHelper.class);
        private List<String> branches = List.of();

        /**
         * The JSON of each branch of the site: four with rates and 'pjc' with every rate in 0.
         */
        Tester withTheSite() {
            for (String code : List.of("pal", "vmo", "cde", "slo", "pjc")) {
                withTheBranch(code, "fecambios_" + code + ".json");
            }
            return this;
        }

        Tester withTheBranch(String remoteCode, String resource) {
            try {
                return withTheResponse(remoteCode,
                        IOUtils.toString(FeCambiosTest.class.getResourceAsStream(resource), StandardCharsets.UTF_8));
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
         * The site answers this branch with a 404.
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

        QueryAsserter query() {
            responses.forEach((code, body) -> Mockito.when(helper.doGet(FeCambios.getBranchUrl(code))).thenReturn(body));
            failing.forEach(code -> Mockito.when(helper.doGet(FeCambios.getBranchUrl(code)))
                    .thenThrow(new AppException(404, "Not found")));

            Place place = new Place("Fe Cambios S.A.", "FeCambios");
            place.setBranches(branches.stream().map(code -> new PlaceBranch(0, code)).toList());

            return new QueryAsserter(new FeCambios(helper).doQuery(place, place.getBranches()));
        }

        /**
         * Runs the query and returns the URLs requested to the site.
         */
        RequestAsserter requests() {
            query();
            return new RequestAsserter(Mockito.mockingDetails(helper).getInvocations().stream()
                    .map(Invocation::getArguments)
                    .map(args -> (String) args[0])
                    .toList());
        }
    }

    private static final class RequestAsserter {

        private final List<String> urls;

        private RequestAsserter(List<String> urls) {
            this.urls = urls;
        }

        RequestAsserter shouldBe(String... expected) {
            assertEquals(List.of(expected), urls);
            return this;
        }
    }
}

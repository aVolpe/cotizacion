package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Arturo Volpe
 * @since 2021-01-02
 */
class MundialTest {

    private static final String[] SITE_BRANCHES = {"globo-center", "jebai", "km-4", "salto-del-guaira", "villa-morra"};

    @Test
    void returnsEveryBranchOfTheSite() {
        tester().withTheSite()
                .withBranches("globo-center", "jebai", "km-4", "salto-del-guaira", "villa-morra")
                .query()
                .shouldReturnBranches("globo-center", "jebai", "km-4", "salto-del-guaira", "villa-morra");
    }

    @Test
    void readsOnlyTheDailyRatesOfEachBranch() {
        tester().withTheSite()
                .withBranches(SITE_BRANCHES)
                .query()
                .shouldHaveOnlyCurrencies("USD", "BRL", "ARS", "EUR", "CLP", "UYU", "CAD", "GBP", "CHF", "JPY");
    }

    @Test
    void quotesEachBranchWithItsOwnRates() {
        tester().withTheSite()
                .withBranches(SITE_BRANCHES)
                .query()
                .shouldQuote("globo-center", "USD", "5.710,00", "5.780,00")
                .shouldQuote("globo-center", "ARS", "3,55", "3,75")
                .shouldQuote("globo-center", "JPY", "29,00", "39,00")
                .shouldQuote("jebai", "USD", "5.700,00", "5.780,00")
                .shouldQuote("jebai", "ARS", "3,20", "4,00")
                .shouldQuote("salto-del-guaira", "BRL", "1.110,00", "1.160,00")
                .shouldQuote("villa-morra", "USD", "5.670,00", "5.800,00");
    }

    @Test
    void skipsTheBranchesThatAreNotInTheSite() {
        tester().withTheSite()
                .withBranches("2", "villa-morra", "6")
                .query()
                .shouldReturnBranches("villa-morra");
    }

    @Test
    void matchesThePanelByIdNotByPosition() {
        tester().withThePage("mundial_reordered_panels.html")
                .withBranches("jebai", "villa-morra")
                .query()
                .shouldQuote("jebai", "USD", "5.700,00", "5.780,00")
                .shouldQuote("villa-morra", "USD", "5.670,00", "5.780,00");
    }

    @Test
    void skipsTheCurrenciesWithoutIsoCode() {
        tester().withThePage("mundial_reordered_panels.html")
                .withBranches("jebai", "villa-morra")
                .query()
                .shouldHaveOnlyCurrencies("USD");
    }

    @Test
    void ignoresTheArbitrageRates() {
        tester().withThePage("mundial_reordered_panels.html")
                .withBranches("jebai", "villa-morra")
                .query()
                .shouldNotQuote("jebai", "BRL")
                .shouldNotQuote("villa-morra", "BRL");
    }

    @Test
    void theRemoteCodeIsTheNormalizedTabTitle() {
        tester().remoteCodeOf("\n\t Salto del  Guairá\t").shouldBe("salto-del-guaira");
    }

    private static Tester tester() {
        return new Tester();
    }

    private static final class Tester {

        private String page;
        private List<String> branches = List.of();

        /**
         * The home page of the site, with five branches.
         */
        Tester withTheSite() {
            return withThePage("mundial_2026.html");
        }

        Tester withThePage(String resource) {
            try {
                page = IOUtils.toString(MundialTest.class.getResourceAsStream(resource), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
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
            HTTPHelper helper = Mockito.mock(HTTPHelper.class);
            Mockito.when(helper.doGet(Mundial.URL)).thenReturn(page);

            Place place = new Place("Mundial Cambios", "MUNDIAL");
            place.setBranches(branches.stream().map(code -> new PlaceBranch(0, code)).toList());

            return new QueryAsserter(new Mundial(helper).doQuery(place, place.getBranches()));
        }

        RemoteCodeAsserter remoteCodeOf(String tabTitle) {
            return new RemoteCodeAsserter(Mundial.toRemoteCode(tabTitle));
        }
    }

    private static final class QueryAsserter {

        private final List<QueryResponse> result;

        private QueryAsserter(List<QueryResponse> result) {
            this.result = result;
        }

        QueryAsserter shouldReturnBranches(String... remoteCodes) {
            assertEquals(List.of(remoteCodes), remoteCodes());
            return this;
        }

        /**
         * Every returned branch has exactly these currencies, in this order.
         */
        QueryAsserter shouldHaveOnlyCurrencies(String... isoCodes) {
            result.forEach(qr -> assertEquals(List.of(isoCodes), isoCodes(qr),
                    "currencies of " + qr.getBranch().getRemoteCode()));
            return this;
        }

        QueryAsserter shouldQuote(String remoteCode, String isoCode, String purchase, String sale) {
            QueryResponseDetail detail = branch(remoteCode).getDetails().stream()
                    .filter(d -> d.getIsoCode().equals(isoCode))
                    .findFirst()
                    .orElseGet(() -> fail("The branch " + remoteCode + " has no " + isoCode + ", only " + isoCodes(branch(remoteCode))));
            assertEquals(Quote.parseLocal(isoCode, purchase), detail.getPurchase(), isoCode + " purchase in " + remoteCode);
            assertEquals(Quote.parseLocal(isoCode, sale), detail.getSale(), isoCode + " sale in " + remoteCode);
            return this;
        }

        QueryAsserter shouldNotQuote(String remoteCode, String isoCode) {
            assertFalse(isoCodes(branch(remoteCode)).contains(isoCode),"The branch " + remoteCode + " has " + isoCode);
            return this;
        }

        private QueryResponse branch(String remoteCode) {
            return result.stream()
                    .filter(qr -> qr.getBranch().getRemoteCode().equals(remoteCode))
                    .findFirst()
                    .orElseGet(() -> fail("There is no branch " + remoteCode + ", only " + remoteCodes()));
        }

        private List<String> remoteCodes() {
            return result.stream().map(qr -> qr.getBranch().getRemoteCode()).toList();
        }

        private static List<String> isoCodes(QueryResponse qr) {
            return qr.getDetails().stream().map(QueryResponseDetail::getIsoCode).toList();
        }
    }

    private static final class RemoteCodeAsserter {

        private final String remoteCode;

        private RemoteCodeAsserter(String remoteCode) {
            this.remoteCode = remoteCode;
        }

        RemoteCodeAsserter shouldBe(String expected) {
            assertEquals(expected, remoteCode);
            return this;
        }
    }
}

package py.com.volpe.cotizacion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import py.com.volpe.cotizacion.domain.Execution;
import py.com.volpe.cotizacion.domain.ExchangeRate;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.repository.ExchangeRateRepository;
import py.com.volpe.cotizacion.repository.ExchangeRateRepository.ByIsoCodeResult;
import py.com.volpe.cotizacion.repository.ExecutionRepository;
import py.com.volpe.cotizacion.repository.PlaceRepository;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * How the queries become exchange rates, and the latest rates the API shows.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({EmbeddedPostgresConfiguration.class, ExchangeRateRecorder.class})
class ExchangeRateRecorderTest {

    private static final long MINUTE = 60_000;

    @Autowired
    private ExchangeRateRecorder recorder;

    @Autowired
    private ExchangeRateRepository rates;

    @Autowired
    private ExecutionRepository executions;

    @Autowired
    private PlaceRepository places;

    private final Date start = new Date(1_760_000_000_000L);

    @Test
    void theSamePriceOnlyMovesTheLastSeen() {
        PlaceBranch branch = branch("MUNDIAL");

        record(branch, 0, "USD", 7000, 7100);
        record(branch, 10, "USD", 7000, 7100);
        record(branch, 20, "USD", 7000, 7100);

        assertThat(history(branch, "USD"))
                .extracting(ExchangeRate::getPurchasePrice, ExchangeRate::getSalePrice, ExchangeRate::getFirstSeen,
                        ExchangeRate::getLastSeen, ExchangeRate::isLatest)
                .containsExactly(tuple(7000L, 7100L, at(0), at(20), true));
    }

    @Test
    void aNewPriceAddsARowAndAnOldPriceAgainAddsAnother() {
        PlaceBranch branch = branch("MUNDIAL");

        record(branch, 0, "USD", 7000, 7100);
        record(branch, 10, "USD", 7010, 7100);
        record(branch, 20, "USD", 7000, 7100);

        assertThat(history(branch, "USD"))
                .extracting(ExchangeRate::getPurchasePrice, ExchangeRate::getFirstSeen, ExchangeRate::getLastSeen,
                        ExchangeRate::isLatest)
                .containsExactly(
                        tuple(7000L, at(0), at(0), false),
                        tuple(7010L, at(10), at(10), false),
                        tuple(7000L, at(20), at(20), true));
    }

    @Test
    void eachBranchAndCurrencyHasItsOwnHistory() {
        PlaceBranch first = branch("MUNDIAL");
        PlaceBranch other = places.findByCode("MUNDIAL").orElseThrow().getBranches().get(1);

        record(first, 0, "USD", 7000, 7100);
        record(other, 0, "USD", 6990, 7090);
        record(first, 10, "EUR", 8000, 8200);
        record(first, 20, "USD", 7000, 7100);

        assertThat(history(first, "USD")).hasSize(1);
        assertThat(history(other, "USD")).hasSize(1);
        assertThat(history(first, "EUR")).hasSize(1);
    }

    @Test
    void placesQueriedAsAWholeHaveNoBranch() {
        Place bank = places.findByCode("GNB").orElseThrow();
        Execution e = executions.save(new Execution());

        recorder.record(response(new QueryResponse(bank), 0, "USD", 7000, 7100), e);
        recorder.record(response(new QueryResponse(bank), 10, "USD", 7000, 7100), e);

        assertThat(rates.findLatest(bank))
                .extracting(ExchangeRate::getBranch, ExchangeRate::getFirstSeen, ExchangeRate::getLastSeen)
                .containsExactly(tuple(null, at(0), at(10)));
    }

    @Test
    void theApiShowsTheRatesSeenByTheLastExecution() {
        PlaceBranch seenByBoth = branch("MUNDIAL");
        PlaceBranch onlyInTheFirst = places.findByCode("MUNDIAL").orElseThrow().getBranches().get(1);

        Execution first = executions.save(new Execution());
        recorder.record(response(new QueryResponse(seenByBoth), 0, "USD", 7000, 7100), first);
        recorder.record(response(new QueryResponse(onlyInTheFirst), 0, "USD", 6990, 7090), first);

        Execution second = executions.save(new Execution());
        recorder.record(response(new QueryResponse(seenByBoth), 10, "USD", 7000, 7100), second);

        List<ByIsoCodeResult> latest = rates.getMaxByPlaceInISO("USD");

        assertThat(latest)
                .extracting(r -> r.getBranch().getId(), ByIsoCodeResult::getQueryId, r -> r.getQueryDate().getTime(),
                        ByIsoCodeResult::getPurchasePrice, ByIsoCodeResult::getSalePrice)
                .containsExactly(tuple(seenByBoth.getId(), second.getId(), at(10).getTime(), 7000L, 7100L));
        assertThat(rates.getMaxByPlaceInISO("EUR")).isEmpty();
    }

    private PlaceBranch branch(String placeCode) {
        return places.findByCode(placeCode).orElseThrow().getBranches().getFirst();
    }

    private void record(PlaceBranch branch, int minute, String iso, long purchase, long sale) {
        recorder.record(response(new QueryResponse(branch), minute, iso, purchase, sale), executions.save(new Execution()));
    }

    private QueryResponse response(QueryResponse response, int minute, String iso, long purchase, long sale) {
        response.setDate(at(minute));
        return response.addDetail(new QueryResponseDetail(purchase, sale, iso));
    }

    private List<ExchangeRate> history(PlaceBranch branch, String iso) {
        return rates.findAll().stream()
                .filter(r -> r.getBranch() != null && r.getBranch().getId() == branch.getId())
                .filter(r -> r.getIsoCode().equals(iso))
                .sorted((a, b) -> a.getFirstSeen().compareTo(b.getFirstSeen()))
                .toList();
    }

    private Date at(int minute) {
        return new Date(start.getTime() + minute * MINUTE);
    }
}

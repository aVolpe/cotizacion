package py.com.volpe.cotizacion.gatherer;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import py.com.volpe.cotizacion.Cotizacion;
import py.com.volpe.cotizacion.EmbeddedPostgresConfiguration;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.repository.PlaceRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs every gatherer against the real sites with the places and branches of the migrations, nothing is stored.
 * <p>
 * Run it with {@code ./mvnw -B test -Dtest=GatherersLiveTest -Dlive=true}, the result of each gatherer is logged
 * (look for 'LIVE').
 */
@Log4j2
@SpringBootTest(classes = Cotizacion.class)
@Import(EmbeddedPostgresConfiguration.class)
@EnabledIfSystemProperty(named = "live", matches = "true")
class GatherersLiveTest {

    @Autowired
    private List<Gatherer> gatherers;

    @Autowired
    private PlaceRepository places;

    @Test
    @Transactional
    void everyGathererReturnsRates() {

        List<String> failed = new ArrayList<>();
        for (Gatherer gatherer : gatherers) {
            try {
                Place place = places.findByCode(gatherer.getCode()).orElseThrow();
                List<QueryResponse> responses = gatherer.doQuery(place, place.getBranches());
                long details = responses.stream().mapToLong(qr -> qr.getDetails().size()).sum();
                log.info("LIVE {}: {} of {} branches, {} rates", gatherer.getCode(), responses.size(),
                        place.getBranches().size(), details);
                if (details == 0) failed.add(gatherer.getCode() + ": no rates");
            } catch (Exception e) {
                log.warn("LIVE {}: {}", gatherer.getCode(), e.toString(), e);
                failed.add(gatherer.getCode() + ": " + e);
            }
        }
        assertTrue(failed.isEmpty(), () -> String.join("\n", failed));
    }
}

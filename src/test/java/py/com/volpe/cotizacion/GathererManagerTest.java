package py.com.volpe.cotizacion;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.gatherer.Gatherer;
import py.com.volpe.cotizacion.repository.ExecutionRepository;
import py.com.volpe.cotizacion.repository.PlaceRepository;
import py.com.volpe.cotizacion.repository.QueryResponseRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author Arturo Volpe
 * @since 5/14/18
 */
class GathererManagerTest {

    @Test
    void init() {


        ExecutionRepository er = Mockito.mock(ExecutionRepository.class);

        Gatherer first = Mockito.mock(Gatherer.class);
        Mockito.when(first.getCode()).thenReturn("p1");

        Gatherer second = Mockito.mock(Gatherer.class);
        Mockito.when(second.getCode()).thenReturn("p2");

        PlaceRepository pr = Mockito.mock(PlaceRepository.class);
        Mockito.when(pr.save(any())).thenAnswer(returnsFirstArg());

        Mockito.when(pr.findByCode("p1")).thenReturn(Optional.of(new Place()));
        Mockito.when(pr.findByCode("p2")).thenReturn(Optional.empty());


        GathererManager manager = new GathererManager(Arrays.asList(first, second), pr, er, null);

        assertEquals(1, manager.init(null).size());
        assertThat(manager.init(null), CoreMatchers.hasItems("p1"));

        assertEquals(0, manager.init("p2").size());

    }

    @Test
    void doQuery() {

        ExecutionRepository er = Mockito.mock(ExecutionRepository.class);

        PlaceRepository pr = Mockito.mock(PlaceRepository.class);
        Mockito.when(pr.findByCode(any())).thenReturn(Optional.of(new Place()));

        QueryResponseRepository qr = Mockito.mock(QueryResponseRepository.class);
        Mockito.when(qr.save(any())).thenAnswer(returnsFirstArg());

        Gatherer first = Mockito.mock(Gatherer.class);
        Mockito.when(first.getCode()).thenReturn("p1");
        Mockito.when(first.doQuery(any(), any())).thenReturn(new ArrayList<>());

        Gatherer second = Mockito.mock(Gatherer.class);
        Mockito.when(second.getCode()).thenReturn("p2");
        Mockito.when(second.doQuery(any(), any())).thenThrow(new AppException(400, "Invalid place"));


        GathererManager manager = new GathererManager(Arrays.asList(first, second), pr, er, qr);

        assertEquals(1, manager.doQuery(null).size());
        assertThat(manager.init(null), CoreMatchers.hasItems("p1"));

        assertEquals(0, manager.doQuery("p2").size());
        assertEquals(1, manager.doQuery("p1").size());
    }
}

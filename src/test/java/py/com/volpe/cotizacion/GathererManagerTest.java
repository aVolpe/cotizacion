package py.com.volpe.cotizacion;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.gatherer.Gatherer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Arturo Volpe
 * @since 5/14/18
 */
public class GathererManagerTest {

    @Test
    public void init() {

        Gatherer first = Mockito.mock(Gatherer.class);
        Gatherer second = Mockito.mock(Gatherer.class);

        Mockito.when(first.get()).thenReturn(new Place());
        Mockito.when(first.getCode()).thenReturn("p1");
        Mockito.when(second.get()).thenThrow(new AppException(400, "Invalid place"));
        Mockito.when(second.getCode()).thenReturn("p2");

        GathererManager manager = new GathererManager(Arrays.asList(first, second));

        Assert.assertEquals(1, manager.init(null).size());
        Assert.assertThat(manager.init(null), CoreMatchers.hasItems("p1"));

        Assert.assertEquals(0, manager.init("p2").size());

    }

    @Test
    public void doQuery() {
        Gatherer first = Mockito.mock(Gatherer.class);
        Gatherer second = Mockito.mock(Gatherer.class);

        Mockito.when(first.doQuery()).thenReturn(new ArrayList<>());
        Mockito.when(first.getCode()).thenReturn("p1");
        Mockito.when(second.doQuery()).thenThrow(new AppException(400, "Invalid place"));
        Mockito.when(second.getCode()).thenReturn("p2");

        GathererManager manager = new GathererManager(Arrays.asList(first, second));

        Assert.assertEquals(1, manager.doQuery(null).size());
        Assert.assertThat(manager.init(null), CoreMatchers.hasItems("p1"));

        Assert.assertEquals(0, manager.doQuery("p2").size());
    }
}
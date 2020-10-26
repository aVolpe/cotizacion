package py.com.volpe.cotizacion.controller;

import org.junit.Test;
import org.mockito.Mockito;
import py.com.volpe.cotizacion.GathererManager;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Arturo Volpe
 * @since 5/14/18
 */
public class PlaceControllerTest {

    @Test
    public void testCalls() {

        GathererManager manager = Mockito.mock(GathererManager.class);
        ScrapController pc = new ScrapController(manager, "a");

        Mockito.when(manager.doQuery(Mockito.anyString())).thenReturn(singleton("a"));
        Mockito.when(manager.init(Mockito.anyString())).thenReturn(singleton("a"));


        assertThat(pc.doQuery("a", "a"), hasItem("a"));
        assertThat(pc.init("a", "a"), hasItem("a"));


    }

}

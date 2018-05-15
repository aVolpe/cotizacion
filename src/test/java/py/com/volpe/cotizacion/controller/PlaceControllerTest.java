package py.com.volpe.cotizacion.controller;

import org.junit.Test;
import org.mockito.Mockito;
import py.com.volpe.cotizacion.GathererManager;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

/**
 * @author Arturo Volpe
 * @since 5/14/18
 */
public class PlaceControllerTest {

    @Test
    public void testCalls() {

        GathererManager manager = Mockito.mock(GathererManager.class);
        PlaceController pc = new PlaceController(manager);

        Mockito.when(manager.doQuery(Mockito.anyString())).thenReturn(singletonList("a"));
        Mockito.when(manager.init(Mockito.anyString())).thenReturn(singletonList("a"));


        assertThat(pc.doQuery("a"), hasItem("a"));
        assertThat(pc.init("a"), hasItem("a"));



    }

}
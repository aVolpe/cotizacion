package py.com.volpe.cotizacion.controller;

import org.junit.Test;
import org.mockito.Mockito;
import py.com.volpe.cotizacion.GathererManager;

import static org.junit.Assert.assertEquals;

/**
 * @author Arturo Volpe
 * @since 5/14/18
 */
public class PlaceControllerTest {

    @Test
    public void testCalls() {

        GathererManager manager = Mockito.mock(GathererManager.class);
        PlaceController pc = new PlaceController(manager);

        Mockito.when(manager.doQuery(Mockito.anyString())).thenReturn("a");
        Mockito.when(manager.init(Mockito.anyString())).thenReturn("a");


        assertEquals(pc.doQuery("a"), "a");
        assertEquals(pc.init("a"), "a");



    }

}
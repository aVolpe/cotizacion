package py.com.volpe.cotizacion.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import py.com.volpe.cotizacion.GathererManager;

import static org.junit.Assert.assertEquals;

/**
 * @author Arturo Volpe
 * @since 5/14/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlaceControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private GathererManager manager;
    @Test
    public void testCalls() {

        Mockito.when(manager.doQuery(Mockito.anyString())).thenReturn("a");
        Mockito.when(manager.init(Mockito.anyString())).thenReturn("a");


        assertEquals(restTemplate.getForEntity("/api/places/init?code=a", String.class).getBody(), "a");
        assertEquals(restTemplate.getForEntity("/api/places/doQuery?code=a", String.class).getBody(), "a");



    }

}
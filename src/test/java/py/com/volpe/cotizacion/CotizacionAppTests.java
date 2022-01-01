package py.com.volpe.cotizacion;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This only test if the application start.
 */
@WebMvcTest @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CotizacionAppTests {

    @Autowired
    private GathererManager manager;

    @Test
    public void contextLoads() {

        assertNotNull(manager);
    }

}

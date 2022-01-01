package py.com.volpe.cotizacion;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This only test if the application start.
 */
@SpringBootTest(classes = Cotizacion.class)
@ExtendWith(SpringExtension.class)
public class CotizacionAppTests {

    @Autowired
    private GathererManager manager;

    @Test
    public void contextLoads() {

        assertNotNull(manager);
    }

}

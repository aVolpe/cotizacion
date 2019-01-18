package py.com.volpe.cotizacion;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This only test if the application start.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExchangeAppTests {

    @Autowired
    private GathererManager manager;

    @Test
    public void contextLoads() {

        Assert.assertNotNull(manager);
    }

}

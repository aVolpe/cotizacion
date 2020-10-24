package py.com.volpe.cotizacion.controller;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Arturo Volpe
 * @since 5/12/18
 */
public class LicenseControllerTest {

    @Test
    public void testNotFound() throws Exception {

        ResourceLoader loader = Mockito.mock(ResourceLoader.class);
        Resource resource = Mockito.mock(Resource.class);

        Mockito.when(loader.getResource(Mockito.anyString())).thenReturn(resource);
        Mockito.when(resource.exists()).thenReturn(false);

        LicenseController lc = new LicenseController(loader);

        ResponseEntity<Object> re = lc.getLicenses();
        Assert.assertEquals(404, re.getStatusCodeValue());
    }

    @Test
    public void testFound() throws Exception {

        ResourceLoader loader = Mockito.mock(ResourceLoader.class);
        Resource resource = Mockito.mock(Resource.class);

        Mockito.when(loader.getResource(Mockito.anyString())).thenReturn(resource);
        Mockito.when(resource.exists()).thenReturn(true);
        Mockito.when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));


        LicenseController lc = new LicenseController(loader);

        ResponseEntity<Object> re = lc.getLicenses();
        Assert.assertEquals(200, re.getStatusCodeValue());
        String body = IOUtils.toString(((InputStreamResource) re.getBody()).getInputStream(), StandardCharsets.UTF_8);
        Assert.assertEquals("test", body);
    }

}

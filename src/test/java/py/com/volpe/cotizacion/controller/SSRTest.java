package py.com.volpe.cotizacion.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import py.com.volpe.cotizacion.Cotizacion;
import py.com.volpe.cotizacion.EmbeddedPostgresConfiguration;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Uses src/test/resources/public/index.html as template, a stand-in for the client's built index.html.
 */
@SpringBootTest(classes = Cotizacion.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(EmbeddedPostgresConfiguration.class)
@ExtendWith(SpringExtension.class)
class SSRTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private ExchangeRateRepository repo;

    @Test
    void preloadsTheRequestedCurrency() {

        Place place = new Place();
        place.setName("Cambios </script><script>alert(1)</script>");
        Mockito.when(repo.getMaxByPlaceInISO("EUR")).thenReturn(Collections.singletonList(
                new ExchangeRateRepository.ByIsoCodeResult(1, place, new PlaceBranch(), 1, new Date(), 1, new BigDecimal("8000"), new BigDecimal("8200"))
        ));

        ResponseEntity<String> response = restTemplate.getForEntity("/?moneda=EUR", String.class);

        assertEquals(200, response.getStatusCode().value());
        String body = response.getBody();
        assertNotNull(body);
        // A JS string literal (parsed by the client), with the JSON quotes escaped
        assertTrue(body.contains("window.ssr = \"{"), body);
        assertTrue(body.contains("\\\"current\\\":{"), body);
        assertTrue(body.contains("\\\"currency\\\":\\\"EUR\\\""), body);
        assertTrue(body.contains("\\\"EUR\\\":{"), body);
        assertTrue(body.contains("8200"), body);
        // Scraped data can't close the script tag
        assertTrue(body.contains("alert(1)"), body);
        assertFalse(body.contains("</script><script>alert(1)"), body);
    }

    @Test
    void clientRoutesServeTheApp() {

        Mockito.when(repo.getMaxByPlaceInISO("USD")).thenReturn(Collections.emptyList());

        for (String path : new String[]{"/", "/map", "/licenses", "/swagger", "/about"}) {
            ResponseEntity<String> response = restTemplate.getForEntity(path, String.class);

            assertEquals(200, response.getStatusCode().value(), path);
            assertTrue(MediaType.TEXT_HTML.isCompatibleWith(response.getHeaders().getContentType()), path);
            assertTrue(response.getBody().contains("<div id=\"app\">"), path);
            assertTrue(response.getBody().contains("\\\"currency\\\":\\\"USD\\\""), path);
        }
    }

    @Test
    void unknownPathsAreStillNotFound() {

        assertEquals(404, restTemplate.getForEntity("/no-existe", String.class).getStatusCode().value());
    }
}

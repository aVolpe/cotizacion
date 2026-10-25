package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Arturo Volpe
 * @since 2026-10-25
 */
@ExtendWith(MockitoExtension.class)
class FamiliarTest {

    @Mock
    private HTTPHelper helper;

    @InjectMocks
    private Familiar familiar;

    @Test
    void doQuery() throws IOException {

        String data = IOUtils.toString(getClass().getResourceAsStream("familiar_cambios.html"), StandardCharsets.UTF_8);
        Mockito.when(helper.doGet("https://www.familiar.com.py/cambios")).thenReturn(data);

        Place p = new Place();
        p.setName("Banco Familiar");

        List<QueryResponse> response = familiar.doQuery(p, Collections.emptyList());

        assertEquals(1, response.size());
        QueryResponse qr = response.get(0);
        assertNull(qr.getBranch());

        // only the cash rates, the 'Cheque / Transf.' ones and the ARS ('Peso', 1 / 5) are ignored
        assertEquals(3, qr.getDetails().size());
        assertDetail(qr.getDetails().get(0), Quote.of("USD", 5640), Quote.of("USD", 5840));
        assertDetail(qr.getDetails().get(1), Quote.of("EUR", 6150), Quote.of("EUR", 6730));
        assertDetail(qr.getDetails().get(2), Quote.of("BRL", 1000), Quote.of("BRL", 1180));
        assertTrue(qr.getDetails().stream().noneMatch(d -> d.getIsoCode().equals("ARS")));
    }

    @Test
    void doQueryWithoutRatesReturnsNothing() {

        Mockito.when(helper.doGet(Mockito.anyString())).thenReturn("<html><body>Mantenimiento</body></html>");

        assertTrue(familiar.doQuery(new Place(), Collections.emptyList()).isEmpty());
    }

    private static void assertDetail(QueryResponseDetail detail, Quote purchase, Quote sale) {
        assertEquals(purchase, detail.getPurchase());
        assertEquals(sale, detail.getSale());
    }
}

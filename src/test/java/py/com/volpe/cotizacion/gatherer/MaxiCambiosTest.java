package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Arturo Volpe
 * @since 1/5/20
 */
@ExtendWith(MockitoExtension.class)
class MaxiCambiosTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private MaxiCambios maxi;

    @Test
    void testFetchAsu() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("maxi_asu.xml"), StandardCharsets.UTF_8);

        when(wsHelper.doGet("http://cotizext.maxicambios.com.py/maxicambios4.xml")).thenReturn(data);

        Place place = new Place("mxi", "MAXI");
        PlaceBranch asu = new PlaceBranch(120, "1");
        List<QueryResponse> result = maxi.doQuery(place, Collections.singletonList(asu));

        assertNotNull(result);
        assertEquals(1, result.size());

        QueryResponse coti = result.get(0);

        assertEquals(15, coti.getDetails().size());

        QueryResponseDetail usd = coti.getDetails()
                .stream()
                .filter(d -> d.getIsoCode().equals("USD"))
                .findFirst().orElseThrow(() -> new AppException(500, "Data not found"));

        assertEquals(Quote.of("USD", 7130), usd.getPurchase());
        assertEquals(Quote.of("USD", 7280), usd.getSale());

        // '.' is the decimal separator in Asunción
        QueryResponseDetail ars = getCurrency(coti, "ARS");
        assertEquals(Quote.of("ARS", "7.20"), ars.getPurchase());
        assertEquals(Quote.of("ARS", "8.20"), ars.getSale());

    }

    @Test
    void testFetchCDE() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("maxi_cde.xml"), StandardCharsets.UTF_8);

        when(wsHelper.doGet("http://cotizext.maxicambios.com.py/cotiza_cde.xml")).thenReturn(data);

        Place place = new Place("mxi", "MAXI");
        PlaceBranch cde = new PlaceBranch(120, "17");
        List<QueryResponse> result = maxi.doQuery(place, Collections.singletonList(cde));

        assertNotNull(result);
        assertEquals(1, result.size());

        QueryResponse coti = result.get(0);

        assertEquals(4, coti.getDetails().size());

        QueryResponseDetail usd = coti.getDetails()
                .stream()
                .filter(d -> d.getIsoCode().equals("USD"))
                .findFirst().orElseThrow(() -> new AppException(500, "Data not found"));

        assertEquals(Quote.of("USD", 7210), usd.getPurchase());
        assertEquals(Quote.of("USD", 7270), usd.getSale());

        // ',' is the decimal separator in CDE
        QueryResponseDetail ars = getCurrency(coti, "ARS");
        assertEquals(Quote.of("ARS", "6.70"), ars.getPurchase());
        assertEquals(Quote.of("ARS", "7.70"), ars.getSale());
    }

    @Test
    void testBranchesShareTheBoards() throws Exception {

        String asu = IOUtils.toString(getClass().getResourceAsStream("maxi_asu.xml"), StandardCharsets.UTF_8);
        String cde = IOUtils.toString(getClass().getResourceAsStream("maxi_cde.xml"), StandardCharsets.UTF_8);

        when(wsHelper.doGet("http://cotizext.maxicambios.com.py/maxicambios4.xml")).thenReturn(asu);
        when(wsHelper.doGet("http://cotizext.maxicambios.com.py/cotiza_cde.xml")).thenReturn(cde);

        Place place = new Place("mxi", "MAXI");
        // Multiplaza, Lago Shopping (CDE), Shopping Costanera (Encarnación) and Casa Central CDE
        List<QueryResponse> result = maxi.doQuery(place, List.of(
                new PlaceBranch(120, "1"),
                new PlaceBranch(121, "18"),
                new PlaceBranch(122, "19"),
                new PlaceBranch(123, "17")));

        assertEquals(4, result.size());
        assertEquals(Quote.of("USD", 7130), getCurrency(result.get(0), "USD").getPurchase());
        assertEquals(Quote.of("USD", 7210), getCurrency(result.get(1), "USD").getPurchase());
        assertEquals(Quote.of("USD", 7130), getCurrency(result.get(2), "USD").getPurchase());
        assertEquals(Quote.of("USD", 7210), getCurrency(result.get(3), "USD").getPurchase());

        verify(wsHelper, times(1)).doGet("http://cotizext.maxicambios.com.py/maxicambios4.xml");
        verify(wsHelper, times(1)).doGet("http://cotizext.maxicambios.com.py/cotiza_cde.xml");
    }

    private static QueryResponseDetail getCurrency(QueryResponse qr, String iso) {
        return qr.getDetails()
                .stream()
                .filter(d -> d.getIsoCode().equals(iso))
                .findFirst().orElseThrow(() -> new AppException(500, "Data not found: " + iso));
    }
}

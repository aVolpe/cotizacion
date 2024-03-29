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

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

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
        PlaceBranch asu = new PlaceBranch(120, "0");
        List<QueryResponse> result = maxi.doQuery(place, Collections.singletonList(asu));

        assertNotNull(result);
        assertEquals(1, result.size());

        QueryResponse coti = result.get(0);

        assertEquals(15, coti.getDetails().size());

        QueryResponseDetail usd = coti.getDetails()
                .stream()
                .filter(d -> d.getIsoCode().equals("USD"))
                .findFirst().orElseThrow(() -> new AppException(500, "Data not found"));

        assertEquals(7130, usd.getPurchasePrice());
        assertEquals(7280, usd.getSalePrice());

    }

    @Test
    void testFetchCDE() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("maxi_cde.xml"), StandardCharsets.UTF_8);

        when(wsHelper.doGet("http://cotizext.maxicambios.com.py/cotiza_cde.xml")).thenReturn(data);

        Place place = new Place("mxi", "MAXI");
        PlaceBranch cde = new PlaceBranch(120, "13");
        List<QueryResponse> result = maxi.doQuery(place, Collections.singletonList(cde));

        assertNotNull(result);
        assertEquals(1, result.size());

        QueryResponse coti = result.get(0);

        assertEquals(4, coti.getDetails().size());

        QueryResponseDetail usd = coti.getDetails()
                .stream()
                .filter(d -> d.getIsoCode().equals("USD"))
                .findFirst().orElseThrow(() -> new AppException(500, "Data not found"));

        assertEquals(7210, usd.getPurchasePrice());
        assertEquals(7270, usd.getSalePrice());

    }
}

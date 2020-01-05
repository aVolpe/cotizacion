package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Arturo Volpe
 * @since 1/5/20
 */
@RunWith(MockitoJUnitRunner.class)
public class MaxiCambiosTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private MaxiCambios maxi;

    @Test
    public void testFetchAsu() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("maxi_asu.xml"), StandardCharsets.UTF_8);

        when(wsHelper.doGet("http://cotizext.maxicambios.com.py/maxicambios4.xml")).thenReturn(data);

        Place place = new Place("mxi", "MAXI");
        PlaceBranch asu = new PlaceBranch(120, "0");
        List<QueryResponse> result = maxi.doQuery(place, Collections.singletonList(asu));

        assertNotNull(result);
        assertEquals(1, result.size());

        QueryResponse coti = result.get(0);

        assertEquals(17, coti.getDetails().size());

        QueryResponseDetail usd = coti.getDetails()
                .stream()
                .filter(d -> d.getIsoCode().equals("USD"))
                .findFirst().orElseThrow(() -> new AppException(500, "Data not found"));

        assertEquals(6300, usd.getPurchasePrice());
        assertEquals(6380, usd.getSalePrice());

    }

    @Test
    public void testFetchCDE() throws Exception {

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

        assertEquals(6310, usd.getPurchasePrice());
        assertEquals(6380, usd.getSalePrice());

    }
}

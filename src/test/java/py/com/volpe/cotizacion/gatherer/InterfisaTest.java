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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Arturo Volpe
 * @since 8/31/18
 */

@RunWith(MockitoJUnitRunner.class)
public class InterfisaTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private Interfisa interfisa;

    @Test
    public void build() throws IOException {


        String data = IOUtils.toString(getClass().getResourceAsStream("interfisa_branches.html"), "UTF-8");
        when(wsHelper.doGet(anyString())).thenReturn(data);

        Place p = interfisa.build();

        assertEquals(43, p.getBranches().size());

        assertEquals("INTERFISA", p.getCode());

        assertThat(p.getBranches().stream().map(PlaceBranch::getName).collect(Collectors.toList()),
                hasItems("CASA MATRIZ", "FERNANDO DE LA MORA", "CIUDAD DEL ESTE CENTRO"));
    }

    @Test
    public void doQuery() throws IOException {

        String data = IOUtils.toString(getClass().getResourceAsStream("interfisa_data.html"), "UTF-8");
        when(wsHelper.doGet(anyString())).thenReturn(data);

        Place p = new Place("TEST", "TEST");
        PlaceBranch pb1 = new PlaceBranch();
        pb1.setName("P1");

        PlaceBranch pb2 = new PlaceBranch();
        pb2.setName("P2");

        p.addBranch(pb1);
        p.addBranch(pb2);

        List<QueryResponse> response = interfisa.doQuery(p, p.getBranches());


        assertNotNull(response);
        assertEquals(2, response.size());


        assertEquals(5750, getDetail(response.get(0), "USD").getPurchasePrice());
        assertEquals(5830, getDetail(response.get(0), "USD").getSalePrice());
        assertEquals(5750, getDetail(response.get(1), "USD").getPurchasePrice());
        assertEquals(5830, getDetail(response.get(1), "USD").getSalePrice());

        assertEquals(1300, getDetail(response.get(0), "BRL").getPurchasePrice());
        assertEquals(1450, getDetail(response.get(0), "BRL").getSalePrice());
        assertEquals(1300, getDetail(response.get(1), "BRL").getPurchasePrice());
        assertEquals(1450, getDetail(response.get(1), "BRL").getSalePrice());

        assertEquals(6500, getDetail(response.get(0), "EUR").getPurchasePrice());
        assertEquals(7000, getDetail(response.get(0), "EUR").getSalePrice());
        assertEquals(6500, getDetail(response.get(1), "EUR").getPurchasePrice());
        assertEquals(7000, getDetail(response.get(1), "EUR").getSalePrice());

        assertEquals(150, getDetail(response.get(0), "ARS").getPurchasePrice());
        assertEquals(190, getDetail(response.get(0), "ARS").getSalePrice());
        assertEquals(150, getDetail(response.get(1), "ARS").getPurchasePrice());
        assertEquals(190, getDetail(response.get(1), "ARS").getSalePrice());
    }

    private QueryResponseDetail getDetail(QueryResponse response, String iso) {

        return response.getDetails()
                .stream()
                .filter(d -> d.getIsoCode().equals(iso))
                .findFirst().orElseThrow(() -> new AppException(500, "ISO not found : " + iso + " en " + response));
    }

    @Test
    public void extractData() {

        String query = "Dirección: 25 de Mayo esq. Paraguarí. N° 417 Tel.: (021) 415 9500 (RA)";
        assertEquals("25 de Mayo esq. Paraguarí. N° 417", interfisa.extractData(query).getFirst());
        assertEquals("(021) 415 9500 (RA)", interfisa.extractData(query).getSecond());

        query = "Dr. J. E. Estigarribia (Campo 9) Dirección: José A. Flores 475 c/ Carlos Antonio López Ruta 7 km 213 Tel.: (0528) 222 861 Cel.: (0981) 975 500";

        assertEquals("(0528) 222 861 Cel.: (0981) 975 500", interfisa.extractData(query).getSecond());
    }

    @Test
    public void parseLocation() {
        String query = "-25.285444, -57.630389";

        assertEquals(-25.285444, interfisa.parseLocation(query).getFirst(), 0.1);
        assertEquals(-57.630389, interfisa.parseLocation(query).getSecond(), 0.1);

    }

    @Test
    public void  mapToISO() {
        assertNull(Interfisa.mapToISO("asdfas"));
        assertEquals("USD", Interfisa.mapToISO("Dólar"));

    }
}
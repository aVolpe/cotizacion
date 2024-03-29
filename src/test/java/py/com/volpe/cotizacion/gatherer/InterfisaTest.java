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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


/**
 * @author Arturo Volpe
 * @since 8/31/18
 */
@ExtendWith(MockitoExtension.class)
class InterfisaTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private Interfisa interfisa;


    @Test
    void doQuery() throws IOException {

        String data = IOUtils.toString(getClass().getResourceAsStream("interfisa_data.json"), StandardCharsets.UTF_8);
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
        assertEquals(1, response.size());


        assertEquals(5910, getDetail(response.get(0), "USD").getPurchasePrice());
        assertEquals(6030, getDetail(response.get(0), "USD").getSalePrice());

        assertEquals(1500, getDetail(response.get(0), "BRL").getPurchasePrice());
        assertEquals(1600, getDetail(response.get(0), "BRL").getSalePrice());

        assertEquals(6400, getDetail(response.get(0), "EUR").getPurchasePrice());
        assertEquals(6900, getDetail(response.get(0), "EUR").getSalePrice());

        assertEquals(130, getDetail(response.get(0), "ARS").getPurchasePrice());
        assertEquals(170, getDetail(response.get(0), "ARS").getSalePrice());
    }

    private QueryResponseDetail getDetail(QueryResponse response, String iso) {

        return response.getDetails()
                .stream()
                .filter(d -> d.getIsoCode().equals(iso))
                .findFirst().orElseThrow(() -> new AppException(500, "ISO not found : " + iso + " en " + response));
    }

    @Test
    void extractData() {

        String query = "Dirección: 25 de Mayo esq. Paraguarí. N° 417 Tel.: (021) 415 9500 (RA)";
        assertEquals("25 de Mayo esq. Paraguarí. N° 417", interfisa.extractData(query).getFirst());
        assertEquals("(021) 415 9500 (RA)", interfisa.extractData(query).getSecond());

        query = "Dr. J. E. Estigarribia (Campo 9) Dirección: José A. Flores 475 c/ Carlos Antonio López Ruta 7 km 213 Tel.: (0528) 222 861 Cel.: (0981) 975 500";

        assertEquals("(0528) 222 861 Cel.: (0981) 975 500", interfisa.extractData(query).getSecond());
    }

    @Test
    void parseLocation() {
        String query = "-25.285444, -57.630389";

        assertEquals(-25.285444, interfisa.parseLocation(query).getFirst(), 0.1);
        assertEquals(-57.630389, interfisa.parseLocation(query).getSecond(), 0.1);

    }

    @Test
    void mapToISO() {
        assertNull(Interfisa.mapToISO("asdfas"));
        assertEquals("USD", Interfisa.mapToISO("Dólar"));

    }
}

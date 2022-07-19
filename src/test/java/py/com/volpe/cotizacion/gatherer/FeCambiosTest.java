package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Arturo Volpe
 * @since 10/5/18
 */
@ExtendWith(MockitoExtension.class)
class FeCambiosTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private FeCambios gatherer;

    @Test
    void doQuery() throws Exception {


        String data = IOUtils.toString(getClass().getResourceAsStream("fecambios_exchange_data.html"), StandardCharsets.UTF_8);
        Mockito.when(wsHelper.doPost(Mockito.any(), Mockito.any())).thenReturn(data);

        Place p = new Place();
        PlaceBranch pb = new PlaceBranch();
        pb.setRemoteCode("8");
        List<QueryResponse> result = gatherer.doQuery(p, Collections.singletonList(pb));

        assertEquals(1, result.size());

        assertNotNull(result.get(0).getDetails());
        assertNotNull(result.get(0).getDate());
        assertEquals(10, result.get(0).getDetails().size());


        QueryResponseDetail qrdAsuncion = result.get(0).getDetails()
                .stream()
                .peek(System.out::println)
                .filter(d -> d.getIsoCode().equals("USD"))
                .findFirst().orElseThrow(() -> new AppException(500, "Data not found"));

        assertEquals(5830, qrdAsuncion.getPurchasePrice());
        assertEquals(5890, qrdAsuncion.getSalePrice());
    }

}

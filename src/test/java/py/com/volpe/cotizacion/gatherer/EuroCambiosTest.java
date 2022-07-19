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
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Arturo Volpe
 * @since 2021-01-02
 */
@ExtendWith(MockitoExtension.class)
class EuroCambiosTest {


    @Mock
    private HTTPHelper helper;

    @InjectMocks
    private EuroCambios gatherer;

    @Test
    void queryTest() throws Exception {

        String main = IOUtils.toString(getClass().getResourceAsStream("euro_cot_1.json"), StandardCharsets.UTF_8);
        String sec = IOUtils.toString(getClass().getResourceAsStream("euro_cot_2.json"), StandardCharsets.UTF_8);

        Map<String, String> params = new HashMap<>();
        params.put("param", "getCotizacionesbySucursal");
        params.put("sucursal", "1");

        Mockito.doReturn(main).when(helper).doPost(
                EuroCambios.EXCHANGE_DATA_URL,
                params
        );

        HashMap<String, String> params2 = new HashMap<>(params);
        params2.put("sucursal", "2");
        Mockito.doReturn(sec).when(helper).doPost(
                EuroCambios.EXCHANGE_DATA_URL,
                params2
        );

        Place p = Place.builder().build();
        PlaceBranch mainBranch = PlaceBranch.builder().name("main").build();
        PlaceBranch sucBranch1 = PlaceBranch.builder().name("Sucursal 1").build();
        PlaceBranch sucBranch2 = PlaceBranch.builder().name("Sucursal 1").build();
        p.setBranches(Arrays.asList(mainBranch, sucBranch1, sucBranch2));
        List<QueryResponse> result = gatherer.doQuery(p, p.getBranches());

        assertNotNull(result);
        assertEquals(3, result.size());
        result.forEach(System.out::println);
    }

}

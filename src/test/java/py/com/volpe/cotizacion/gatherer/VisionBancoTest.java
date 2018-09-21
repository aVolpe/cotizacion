package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Arturo Volpe
 * @since 9/21/18
 */
@RunWith(MockitoJUnitRunner.class)
public class VisionBancoTest {

    @Mock
    private HTTPHelper helper;

    @InjectMocks
    private VisionBanco visionBanco;

    @Test
    public void doQuery() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("vision_homepage.html"), "UTF-8");

        Mockito.when(helper.doGet(Mockito.any())).thenReturn(data);

        Place p = new Place();
        p.setName("Vision");

        List<QueryResponse> response = visionBanco.doQuery(p, Collections.emptyList());

        assertTrue(!response.isEmpty());
        assertTrue(!response.get(0).getDetails().isEmpty());


        assertThat(
                response.get(0).getDetails().stream().map(QueryResponseDetail::getIsoCode).collect(Collectors.toList()),
                hasItems("EUR", "USD", "BRL", "ARS")
        );

        assertThat(
                response.get(0).getDetails().stream().map(QueryResponseDetail::getPurchasePrice).collect(Collectors.toList()),
                hasItems(5785L, 1350L, 140L, 6550L)
        );

        assertThat(
                response.get(0).getDetails().stream().map(QueryResponseDetail::getSalePrice).collect(Collectors.toList()),
                hasItems(5830L, 1410L, 160L, 6850L)
        );

        System.out.println(response.get(0).getDetails());
    }

    @Test
    public void build() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("vision_branches.json"), "UTF-8");

        Mockito.when(helper.doGet(Mockito.any())).thenReturn(data);

        Place p = visionBanco.build();

        System.out.println(p);
    }
}
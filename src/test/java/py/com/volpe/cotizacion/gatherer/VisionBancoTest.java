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

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Arturo Volpe
 * @since 9/21/18
 */
@ExtendWith(MockitoExtension.class)
class VisionBancoTest {

    @Mock
    private HTTPHelper helper;

    @InjectMocks
    private VisionBanco visionBanco;

    @Test
    void doQuery() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("vision_homepage.html"), StandardCharsets.UTF_8);

        Mockito.when(helper.doGet(Mockito.any())).thenReturn(data);

        Place p = new Place();
        p.setName("Vision");

        List<QueryResponse> response = visionBanco.doQuery(p, Collections.emptyList());

        assertFalse(response.isEmpty());
        assertFalse(response.get(0).getDetails().isEmpty());


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

}

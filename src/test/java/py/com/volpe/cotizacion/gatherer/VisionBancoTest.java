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
                response.get(0).getDetails().stream().map(QueryResponseDetail::getPurchase).collect(Collectors.toList()),
                hasItems(Quote.of("USD", 5785), Quote.of("BRL", 1350), Quote.of("ARS", 140), Quote.of("EUR", 6550))
        );

        assertThat(
                response.get(0).getDetails().stream().map(QueryResponseDetail::getSale).collect(Collectors.toList()),
                hasItems(Quote.of("USD", 5830), Quote.of("BRL", 1410), Quote.of("ARS", 160), Quote.of("EUR", 6850))
        );

        System.out.println(response.get(0).getDetails());
    }

}

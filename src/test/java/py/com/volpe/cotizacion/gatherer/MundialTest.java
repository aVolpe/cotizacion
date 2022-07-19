package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
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
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Arturo Volpe
 * @since 2021-01-02
 */
@ExtendWith(MockitoExtension.class)
class MundialTest {


    @Mock
    private HTTPHelper helper;

    @InjectMocks
    private Mundial gatherer;


    @Test
    void query() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("mundial_6.html"), StandardCharsets.UTF_8);
        Mockito.when(helper.doGet(Mockito.any(), Mockito.anyInt())).thenReturn(data);

        Place p = Place.builder().name("MUNDIAL").build();
        PlaceBranch branch = PlaceBranch.builder().remoteCode("6").build();
        p.setBranches(Collections.singletonList(branch));

        List<QueryResponse> result = gatherer.doQuery(p, p.getBranches());

        assertNotNull(result);
        assertEquals(1, result.size());
        System.out.println(result);

        QueryResponse details = result.get(0);
        List<QueryResponseDetail> exchanges = details.getDetails();
        assertFalse(exchanges.isEmpty());

        exchanges.forEach(System.out::println);
    }

    @Test
    void parseTest() {
        assertEquals(7000L, Mundial.parse("7.000"), 0);
        assertEquals(6780L, Mundial.parse("6.780"), 0);

    }
}

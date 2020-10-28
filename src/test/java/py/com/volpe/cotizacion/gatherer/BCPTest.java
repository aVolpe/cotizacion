package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Arturo Volpe
 * @since 10/28/20
 */
@RunWith(MockitoJUnitRunner.class)
public class BCPTest {

    @InjectMocks
    private BCP bcp;
    @Mock
    private HTTPHelper wsHelper;

    @Test
    public void doQuery() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("bcp.html"), StandardCharsets.UTF_8);
        when(wsHelper.doGet(anyString(), anyInt(), any())).thenReturn(data);

        Place p = bcp.build();
        assertNotNull(p);
        assertNotNull(p.getBranches());
        assertFalse(p.getBranches().isEmpty());

        List<QueryResponse> result = bcp.doQuery(p, p.getBranches());

        assertNotNull(result);
        assertEquals(1, result.size());

        QueryResponse first = result.get(0);


        assertEquals(7028, getCurrency(first, "USD").getPurchasePrice());
        assertEquals(89, getCurrency(first, "ARS").getPurchasePrice());

    }

    private QueryResponseDetail getCurrency(QueryResponse first, String iso) {
        return first.getDetails()
                .stream()
                .peek(System.out::println)
                .filter(d -> d.getIsoCode().equals(iso))
                .findFirst().orElseThrow(() -> new IllegalStateException("Currency not found: " + iso));
    }

}

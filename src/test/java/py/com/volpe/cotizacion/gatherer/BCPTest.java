package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Arturo Volpe
 * @since 10/28/20
 */
@ExtendWith(MockitoExtension.class)
class BCPTest {

    @InjectMocks
    private BCP bcp;
    @Mock
    private HTTPHelper wsHelper;

    @Test
    void doQuery() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("bcp.html"), StandardCharsets.UTF_8);
        when(wsHelper.doGet(anyString(), anyInt(), any(), anyBoolean())).thenReturn(data);

        Place p = build();
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

    public Place build() {
        Place p = Place.builder()
                .name("Banco Central del Paraguay (Cotización referencial)")
                .type(Place.Type.BANK)
                .code("BCP")
                .build();

        PlaceBranch pb = PlaceBranch.builder()
                .name("Central")
                .latitude(-25.2781319)
                .longitude(-57.5765498)
                .phoneNumber("+59521608011")
                .email("nfo@bcp.gov.py ")
                .remoteCode("01")
                .schedule("")
                .image("https://www.bcp.gov.py/userfiles/images/banners/slider-background-01.jpg")
                .place(p)
                .build();

        p.setBranches(Collections.singletonList(pb));
        return p;
    }
}

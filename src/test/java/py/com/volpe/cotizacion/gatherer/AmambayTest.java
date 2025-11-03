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

@ExtendWith(MockitoExtension.class)
class AmambayTest {

    @InjectMocks
    private Amambay amambay;

    @Mock
    private HTTPHelper wsHelper;

    @Test
    void doQuery() throws Exception {
        String data = IOUtils.toString(getClass().getResourceAsStream("basa.html"), StandardCharsets.UTF_8);
        when(wsHelper.doGet(anyString(), anyInt(), any(), anyBoolean())).thenReturn(data);

        Place p = build();
        assertNotNull(p);
        assertNotNull(p.getBranches());
        assertFalse(p.getBranches().isEmpty());

        List<QueryResponse> result = amambay.doQuery(p, p.getBranches());

        assertNotNull(result);
        assertEquals(1, result.size());

        QueryResponse first = result.get(0);

        // From basa.html:
        // dolar: Compra 6.940 -> 6940
        // reales: Compra 1.210 -> 1210
        // pesosargentinos: Compra 1 -> 1
        // euros: Compra 7.820 -> 7820

        assertEquals(6940L, getCurrency(first, "USD").getPurchasePrice());
        assertEquals(1210L, getCurrency(first, "BRL").getPurchasePrice());
        assertEquals(1L, getCurrency(first, "ARS").getPurchasePrice());
        assertEquals(7820L, getCurrency(first, "EUR").getPurchasePrice());
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
                .name("Banco Basa")
                .type(Place.Type.BANK)
                .code("AMAMBAY")
                .build();

        PlaceBranch pb = PlaceBranch.builder()
                .name("Main")
                .remoteCode("")
                .place(p)
                .build();

        p.setBranches(Collections.singletonList(pb));
        return p;
    }
}


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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Arturo Volpe
 * @since 2022-07-19
 */
@ExtendWith(MockitoExtension.class)
class MercosurTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private Mercosur mercosur;

    @Test
    void doQueryTest() throws IOException {

        String stringData = IOUtils.toString(getClass().getResourceAsStream("mercosur.html"), StandardCharsets.UTF_8);

        when(wsHelper.doGet(anyString())).thenReturn(stringData);

        Place p = build();
        List<QueryResponse> data = mercosur.doQuery(p, p.getBranches());

        assertEquals(1, data.size()); // one branch
        List<QueryResponseDetail> exchanges = data.get(0).getDetails();

        QueryResponseDetail usd = exchanges.stream().filter(f -> Objects.equals(f.getIsoCode(), "USD")).findFirst().orElseThrow();
        assertEquals(7380, usd.getPurchasePrice());
        assertEquals(7440, usd.getSalePrice());
        QueryResponseDetail brl = exchanges.stream().filter(f -> Objects.equals(f.getIsoCode(), "BRL")).findFirst().orElseThrow();
        assertEquals(1475, brl.getPurchasePrice());
        assertEquals(1500, brl.getSalePrice());
        QueryResponseDetail eur = exchanges.stream().filter(f -> Objects.equals(f.getIsoCode(), "EUR")).findFirst().orElseThrow();
        assertEquals(7550, eur.getPurchasePrice());
        assertEquals(8210, eur.getSalePrice());
        QueryResponseDetail arg = exchanges.stream().filter(f -> Objects.equals(f.getIsoCode(), "ARS")).findFirst().orElseThrow();
        assertEquals(7, arg.getPurchasePrice());
        assertEquals(9, arg.getSalePrice());

    }

    @Test
    void getCurrencyTest() {
        assertEquals("USD", new Mercosur(null).getCurrency("Dólar x Guarani"));
        assertEquals("BRL", new Mercosur(null).getCurrency("Real x Guarani"));
        assertNull(new Mercosur(null).getCurrency("Dólar x Real"));

    }

    public Place build() {

        Place place = new Place("MERCOSUR", "MERCOSUR");
        place.setBranches(List.of(new PlaceBranch(1, "1")));
        return place;

    }

}

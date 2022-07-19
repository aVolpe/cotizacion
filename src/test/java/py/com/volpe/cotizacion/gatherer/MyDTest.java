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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Arturo Volpe
 * @since 10/7/18
 */
@ExtendWith(MockitoExtension.class)
class MyDTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private MyD gatherer;

    @Test
    void doQuery() throws IOException {

        String data = IOUtils.toString(getClass().getResourceAsStream("myd_data.html"), StandardCharsets.UTF_8);

        Mockito.when(wsHelper.doGet(Mockito.any())).thenReturn(data);
        Place p = build();

        List<QueryResponse> result = gatherer.doQuery(p, p.getBranches());

        assertEquals(3, result.size());

        QueryResponse as = result.get(0);
        QueryResponse vm = result.get(1);
        QueryResponse cde = result.get(2);

        assertEquals(0, cde.getDetails().size());
        assertEquals(11, vm.getDetails().size());
        assertEquals(11, as.getDetails().size());

        assertEquals(5850, as.getDetails().get(0).getPurchasePrice());
        assertEquals(5870, as.getDetails().get(0).getSalePrice());
        assertEquals("USD", as.getDetails().get(0).getIsoCode());

        assertEquals(1465, as.getDetails().get(1).getPurchasePrice());
        assertEquals(1485, as.getDetails().get(1).getSalePrice());
        assertEquals("BRL", as.getDetails().get(1).getIsoCode());

        assertEquals(170, as.getDetails().get(10).getPurchasePrice());
        assertEquals(260, as.getDetails().get(10).getSalePrice());
        assertEquals("UYU", as.getDetails().get(10).getIsoCode());

        assertEquals(5850, vm.getDetails().get(0).getPurchasePrice());
        assertEquals(5870, vm.getDetails().get(0).getSalePrice());
        assertEquals("USD", vm.getDetails().get(0).getIsoCode());

        assertEquals(1465, vm.getDetails().get(1).getPurchasePrice());
        assertEquals(1485, vm.getDetails().get(1).getSalePrice());
        assertEquals("BRL", vm.getDetails().get(1).getIsoCode());

        assertEquals(170, vm.getDetails().get(10).getPurchasePrice());
        assertEquals(260, vm.getDetails().get(10).getSalePrice());
        assertEquals("UYU", vm.getDetails().get(10).getIsoCode());
    }

    @Test
    void getCurrencyName() {
        assertEquals("ar", MyD.getCurrencyName("https://www.mydcambios.com.py/uploads/ar.png"));
        assertEquals("descarga", MyD.getCurrencyName("https://www.mydcambios.com.py/uploads/descarga.png"));
        assertEquals("us-1", MyD.getCurrencyName("https://www.mydcambios.com.py/uploads/us-1.png"));


    }

    public Place build() {


        Place place = new Place("Cambios MyD", "MYD");

        PlaceBranch matriz = new PlaceBranch();
        matriz.setName("Casa Matriz");
        matriz.setRemoteCode("2");
        matriz.setPhoneNumber("021451377/9");
        matriz.setLatitude(-25.281474);
        matriz.setLongitude(-57.637259);
        matriz.setImage("https://www.mydcambios.com.py/uploads/sucursal/2/_principal_myd_cambios_matriz.jpg");


        PlaceBranch villa = new PlaceBranch();
        villa.setName("Villa Morra");
        villa.setRemoteCode("3");
        villa.setPhoneNumber("021-662537 /  021-609662");
        villa.setLatitude(-25.294182);
        villa.setLongitude(-57.579190);
        villa.setImage("https://www.mydcambios.com.py/uploads/sucursal/3/_principal_img_20160205_wa0007.jpg");

        PlaceBranch cde = new PlaceBranch();
        cde.setName("Agencia KM4 - Cotizaciones");
        cde.setRemoteCode("4");
        cde.setPhoneNumber("021-662537 /  021-609662");
        cde.setLatitude(-25.508799);
        cde.setLongitude(-54.639613);
        cde.setImage("https://www.mydcambios.com.py/uploads/sucursal/4/_principal_img_20160218_wa0060.jpg");

        place.setBranches(Arrays.asList(matriz, villa, cde));

        return place;

    }
}

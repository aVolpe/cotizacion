package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Arturo Volpe
 * @since 5/15/18
 */
@RunWith(MockitoJUnitRunner.class)
public class AlberdiTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private Alberdi alberdi;

    @Before
    public void init() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("alberdi_data.json"), "UTF-8");

        when(wsHelper.doGet(anyString())).thenReturn(data);
    }

    @Test
    public void create() throws Exception {


        Place created = alberdi.build();

        assertNotNull(created);
        assertNotNull(alberdi.getCode(), created.getCode());
        assertNotNull(created.getName());

        assertEquals(7, created.getBranches().size());

        assertThat(created.getBranches().stream().map(PlaceBranch::getName).collect(Collectors.toList()),
                hasItems("Villa Morra", "San Lorenzo"));

        for (PlaceBranch pb : created.getBranches()) {
            assertNotEquals("A branch is not mapped", pb.getName(), pb.getRemoteCode());
        }

    }

    @Test
    public void failToReadFile() {

        when(wsHelper.doGet(anyString())).thenReturn("[]");
        try {
            alberdi.getParsedData();
            fail("this should fail, the file is invalid");
        } catch (AppException ae) {
            assertEquals(500, ae.getNumber());
        }
    }

    @Test
    public void doQuery() {
        Place place = alberdi.build();

        List<QueryResponse> data = alberdi.doQuery(place, place.getBranches());

        assertEquals(7, data.size());
        for (QueryResponse qr : data) {
            assertNotNull(qr.getPlace());
            assertNotNull(qr.getDetails());
            assertNotNull(qr.getBranch());
            assertNotNull(qr.getDate());
            assertEquals(4, qr.getDetails().size());

            if (qr.getBranch().getRemoteCode().equalsIgnoreCase("asuncion")) {

                QueryResponseDetail qrdAsuncion = qr.getDetails()
                        .stream()
                        .filter(d -> d.getIsoCode().equals("USD"))
                        .findFirst().orElseThrow(() -> new AppException(500, "Data not found"));

                assertEquals(5510, qrdAsuncion.getPurchasePrice());
                assertEquals(5535, qrdAsuncion.getSalePrice());
            }

            assertThat(
                    qr.getDetails().stream().map(QueryResponseDetail::getIsoCode).collect(Collectors.toList()),
                    hasItems("EUR", "USD", "BRL", "ARS")

            );
        }
    }

}
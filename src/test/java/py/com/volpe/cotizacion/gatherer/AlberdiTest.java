package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * @author Arturo Volpe
 * @since 5/15/18
 */
@ExtendWith(MockitoExtension.class)
public class AlberdiTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private Alberdi alberdi;

    @Test
    public void create() throws Exception {

        Place created = alberdi.build();

        assertNotNull(created);
        assertNotNull(alberdi.getCode(), created.getCode());
        assertNotNull(created.getName());

        assertEquals(5, created.getBranches().size());

        assertThat(created.getBranches().stream().map(PlaceBranch::getName).collect(Collectors.toList()),
                hasItems("Villa Morra", "CDE KM 4"));

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
    public void doQuery() throws Exception {

        String stringData = IOUtils.toString(getClass().getResourceAsStream("alberdi_data.json"), "UTF-8");

        when(wsHelper.doGet(anyString())).thenReturn(stringData);


        Place place = alberdi.build();

        List<QueryResponse> data = alberdi.doQuery(place, place.getBranches());

        assertEquals(5, data.size());
        for (QueryResponse qr : data) {
            assertNotNull(qr.getPlace());
            assertNotNull(qr.getDetails());
            assertNotNull(qr.getBranch());
            assertNotNull(qr.getDate());
            assertFalse(qr.getDetails().isEmpty(), "The branch " + qr.getBranch() + " is empty");

            if (qr.getBranch().getRemoteCode().equalsIgnoreCase("villamorra")) {

                QueryResponseDetail qrdAsuncion = qr.getDetails()
                        .stream()
                        .filter(d -> d.getIsoCode().equals("USD"))
                        .findFirst().orElseThrow(() -> new AppException(500, "Data not found"));

                assertEquals(6800, qrdAsuncion.getPurchasePrice());
                assertEquals(6890, qrdAsuncion.getSalePrice());
            }

            assertThat(
                    qr.getDetails().stream().map(QueryResponseDetail::getIsoCode).collect(Collectors.toList()),
                    hasItems("EUR", "USD", "BRL", "ARS")

            );
        }
    }

}
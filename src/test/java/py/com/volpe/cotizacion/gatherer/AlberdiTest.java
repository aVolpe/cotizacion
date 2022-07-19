package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


/**
 * @author Arturo Volpe
 * @since 5/15/18
 */
@ExtendWith(MockitoExtension.class)
class AlberdiTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private Alberdi alberdi;

    @Test
    void failToReadFile() {

        when(wsHelper.doGet(anyString())).thenReturn("[]");
        try {
            alberdi.getParsedData();
            fail("this should fail, the file is invalid");
        } catch (AppException ae) {
            assertEquals(500, ae.getNumber());
        }
    }

    @Test
    void doQuery() throws Exception {

        String stringData = IOUtils.toString(getClass().getResourceAsStream("alberdi_data.json"), StandardCharsets.UTF_8);

        when(wsHelper.doGet(anyString())).thenReturn(stringData);


        Place place = build();

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

    public Place build() {

        Place place = new Place("Cambios Alberdi", "ALBERDI_2");

        Map<String, PlaceBranch> result = getBranchData();

        place.setBranches(new ArrayList<>(result.values()));

        return place;

    }

    private Map<String, PlaceBranch> getBranchData() {

        InputStream fileIS = getClass().getClassLoader().getResourceAsStream("alberdi_branch_data.json");

        try {
            return new ObjectMapper()
                    .readValue(fileIS, new TypeReference<Map<String, PlaceBranch>>() {
                    });
        } catch (IOException e) {
            throw new AppException(500, "Can't read branch file", e);
        }
    }

}

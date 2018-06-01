package py.com.volpe.cotizacion.gatherer;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.WSHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.repository.PlaceRepository;
import py.com.volpe.cotizacion.repository.QueryResponseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Arturo Volpe
 * @since 5/15/18
 */
@RunWith(MockitoJUnitRunner.class)
public class AlberdiTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private QueryResponseRepository queryResponseRepository;

    @Mock
    private WSHelper wsHelper;

    @InjectMocks
    private Alberdi alberdi;

    @Before
    public void init() throws Exception {

        String data = IOUtils.toString(getClass().getResourceAsStream("alberdi_data.json"), "UTF-8");

        when(wsHelper.getDataWithoutSending(anyString())).thenReturn(data);
        when(placeRepository.save(any())).thenAnswer(returnsFirstArg());
        when(queryResponseRepository.save(any())).thenAnswer(returnsFirstArg());
    }

    @Test
    public void create() throws Exception {


        Place created = alberdi.get();

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

        when(wsHelper.getDataWithoutSending(anyString())).thenReturn("[]");
        try {
            alberdi.getParsedData();
            Assert.fail();
        } catch(AppException ae) {
            assertEquals(500, ae.getNumber());
        }
    }


    @Test
    public void doQuery() throws Exception {
        Place place = alberdi.get();
        when(placeRepository.findByCode(anyString())).thenReturn(Optional.of(place));


        List<QueryResponse> data = alberdi.doQuery();

        assertEquals(7, data.size());
        for (QueryResponse qr : data) {
            assertNotNull(qr.getPlace());
            assertNotNull(qr.getDetails());
            assertNotNull(qr.getBranch());
            assertNotNull(qr.getDate());
            assertEquals(4, qr.getDetails().size());

            assertThat(
                    qr.getDetails().stream().map(QueryResponseDetail::getIsoCode).collect(Collectors.toList()),
                    hasItems("EUR", "USD", "BRL", "ARS")

            );
        }
    }

}
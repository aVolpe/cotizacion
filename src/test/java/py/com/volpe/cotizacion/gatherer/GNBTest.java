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
public class GNBTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private GNB gnb;

    @Test
    public void create() throws Exception {

        Place created = gnb.build();

        assertNotNull(created);
        assertNotNull(gnb.getCode(), created.getCode());
        assertNotNull(created.getName());

        assertEquals(6, created.getBranches().size());

        assertThat(created.getBranches().stream().map(PlaceBranch::getName).collect(Collectors.toList()),
                hasItems("Villa morra", "Casa central"));

        for (PlaceBranch pb : created.getBranches()) {
            assertNotEquals("A branch is not mapped", pb.getName(), pb.getRemoteCode());
        }

    }

    @Test
    public void doQuery() throws Exception {

        String stringData = IOUtils.toString(getClass().getResourceAsStream("gnb_data.json"), "UTF-8");

        when(wsHelper.doGet(anyString())).thenReturn(stringData);


        Place place = gnb.build();

        List<QueryResponse> data = gnb.doQuery(place, place.getBranches());

        assertEquals(1, data.size());
        for (QueryResponse qr : data) {
            assertNotNull(qr.getPlace());
            assertNotNull(qr.getDetails());
            assertNotNull(qr.getDate());
            assertFalse(qr.getDetails().isEmpty(), "The branch " + qr.getBranch() + " is empty");
            assertEquals(2, qr.getDetails().size());

            assertThat(
                    qr.getDetails().stream().map(QueryResponseDetail::getIsoCode).collect(Collectors.toList()),
                    hasItems("EUR", "USD")
            );

            QueryResponseDetail usd = qr.getDetails().stream().filter(q -> q.getIsoCode().equals("USD")).findFirst().orElse(null);
            QueryResponseDetail eur = qr.getDetails().stream().filter(q -> q.getIsoCode().equals("EUR")).findFirst().orElse(null);

            assertEquals(6730, usd.getPurchasePrice());
            assertEquals(6950, usd.getSalePrice());

            assertEquals(7400, eur.getPurchasePrice());
            assertEquals(8100, eur.getSalePrice());

            
        }
    }

}
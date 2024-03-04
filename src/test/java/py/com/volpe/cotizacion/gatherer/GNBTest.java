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
import java.util.ArrayList;
import java.util.List;
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
class GNBTest {

    @Mock
    private HTTPHelper wsHelper;

    @InjectMocks
    private GNB gnb;


    @Test
    void doQuery() throws Exception {

        String stringData = IOUtils.toString(getClass().getResourceAsStream("gnb_data.json"), StandardCharsets.UTF_8);

        when(wsHelper.doGet(anyString())).thenReturn(stringData);


        Place place = build();

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

            assertEquals(7220, usd.getPurchasePrice());
            assertEquals(7380, usd.getSalePrice());

            assertEquals(7570, eur.getPurchasePrice());
            assertEquals(8310, eur.getSalePrice());


        }
    }

    public Place build() {
        Place p = Place.builder()
                .name("Banco GNB")
                .type(Place.Type.BANK)
                .code("GNB")
                .branches(new ArrayList<>())
                .build();

        p.addBranch(PlaceBranch.builder()
                .name("Casa central")
                .latitude(-25.283026)
                .longitude(-57.5637487)
                .phoneNumber("+595216183000")
                .remoteCode("central")
                .schedule("8:30 a 13:30 - Lunes a viernes")
                .image("https://lh5.googleusercontent.com/p/AF1QipN6YrTaGexEo1YJMli9pK4ZTPQJfN07ufe2pn40=w408-h544-k-no")
                .build());

        p.addBranch(PlaceBranch.builder()
                .name("Villa morra")
                .latitude(-25.2946981)
                .longitude(-57.5802288)
                .phoneNumber("+5956183000")
                .remoteCode("villamorra")
                .schedule("8:30 a 13:30 - Lunes a viernes")
                .image("https://lh5.googleusercontent.com/p/AF1QipMys851Z_uy9r65DYj68o38ALJ4GvtfPPCy6mh9=w408-h306-k-no")
                .build());

        p.addBranch(PlaceBranch.builder()
                .name("Eusebio Ayala")
                .latitude(-25.3089527)
                .longitude(-57.5988751)
                .remoteCode("eusebioayala")
                .phoneNumber("+595216183001")
                .schedule("8:30 a 13:30 - Lunes a viernes")
                .image("https://lh5.googleusercontent.com/p/AF1QipNL73EsvtPvZcebT71u7mnrmwoOEJtKmx1aqoCY=w426-h240-k-no")
                .build());

        p.addBranch(PlaceBranch.builder()
                .name("San Lorenzo")
                .latitude(-25.343856)
                .longitude(-57.5074085)
                .remoteCode("sanlorenzo")
                .phoneNumber("+59521570475")
                .schedule("8:30 a 13:30 - Lunes a viernes")
                .image("https://lh5.googleusercontent.com/p/AF1QipPZwGJsIZeoRQ3SVbs9Kmy62oh5vzjBpBNLgdVh=w408-h544-k-no")
                .build());

        p.addBranch(PlaceBranch.builder()
                .name("Encarnacion")
                .latitude(-27.3261481)
                .longitude(-55.8696665)
                .remoteCode("encarnacion")
                .phoneNumber("+59571202737")
                .schedule("8:30 a 13:30 - Lunes a viernes")
                .image("https://lh5.googleusercontent.com/p/AF1QipPyr9me5BzUPouRx3smaBanU7OwgVWKpYoC2Pzp=w408-h306-k-no")
                .build());

        p.addBranch(PlaceBranch.builder()
                .name("Ciudad del Este")
                .latitude(-25.5083572)
                .longitude(-54.6386737)
                .phoneNumber("+595214176509")
                .remoteCode("cde")
                .schedule("8:30 a 13:30 - Lunes a viernes")
                .image("https://lh5.googleusercontent.com/p/AF1QipPUgbipb9hOFz12zvHzmVLB3V3vroAtErRaKWoK=w408-h545-k-no")
                .build());

        return p;
    }

}

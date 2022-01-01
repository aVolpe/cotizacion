package py.com.volpe.cotizacion.gatherer;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.Place.Type;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Arturo Volpe
 * @since 10/28/20
 */
@Service
@RequiredArgsConstructor
public class GNB implements Gatherer {

    private final HTTPHelper helper;

    private static final String WS_URL = "https://www.bancognbcaminamosjuntos.com.py/Yaguarete/public/quotations";

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {
        
        QueryResponse toRet = new QueryResponse(place);

        List<Root> parsed = getParsedData();

        for (Root info: parsed) {
            toRet.addDetail(new QueryResponseDetail(
                info.getCashBuyPrice(),
                info.getCashSellPrice(),
                info.getCurrency().getCode()
            ));
        }

        return Collections.singletonList(toRet);
    }

    protected List<Root> getParsedData() {
        try {
            String queryResult = helper.doGet(WS_URL);
            return buildMapper()
                    .readValue(queryResult, new TypeReference<List<Root>>() {
                    });
        } catch (IOException e) {
            throw new AppException(500, "cant parse the result of GNB ws", e);
        }
    }

    private ObjectMapper buildMapper() {
        return new ObjectMapper();
    }

    @Override
    public Place build() {
        Place p = Place.builder()
                .name("Banco GNB")
                .type(Type.BANK)
                .code(getCode())
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

    @Override
    public String getCode() {
        return "GNB";
    }


    protected Long parseAmount(String amount) {
        return Long.parseLong(amount.substring(0, amount.indexOf(','))
                .replace(".", ""));

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Currency {
        public String code;
        public String description;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root {
        public Currency currency;
        public int electronicBuyPrice;
        public int electronicSellPrice;
        public int cashBuyPrice;
        public int cashSellPrice;
        public int checkBuyPrice;
        public int checkSellPrice;
    }
    
}

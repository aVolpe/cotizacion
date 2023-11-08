package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Arturo Volpe
 * @since 10/28/20
 */
@Service
@RequiredArgsConstructor
public class GNB implements Gatherer {

    private final HTTPHelper helper;

    private static final String WS_URL = "https://www.bancognb.com.py/Yaguarete/public/quotations";

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        QueryResponse toRet = new QueryResponse(place);

        List<Root> parsed = getParsedData();

        for (Root info : parsed) {
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

package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.math.BigDecimal;
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

    private static final String WS_URL = "https://www.bancognb.com.py/public/currency_quotations";

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        QueryResponse toRet = new QueryResponse(place);

        Root parsed = getParsedData();

        for (Exchange info : parsed.getExchangeRates()) {
            toRet.addDetail(new QueryResponseDetail(
                    Quote.of(info.getCurrencyCode(), info.getCashBuyPrice()),
                    Quote.of(info.getCurrencyCode(), info.getCashSellPrice())
            ));
        }

        return Collections.singletonList(toRet);
    }

    protected Root getParsedData() {
        try {
            String queryResult = helper.doGet(WS_URL);
            return buildMapper()
                    .readValue(queryResult, Root.class);
        } catch (JacksonException e) {
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


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Root {
        private List<Exchange> exchangeRates;
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Exchange {
        public String currencyCode;
        public String currencyDesc;
        public BigDecimal electronicBuyPrice;
        public BigDecimal electronicSellPrice;
        public BigDecimal cashBuyPrice;
        public BigDecimal cashSellPrice;
        public BigDecimal checkBuyPrice;
        public BigDecimal checkSellPrice;
    }

}

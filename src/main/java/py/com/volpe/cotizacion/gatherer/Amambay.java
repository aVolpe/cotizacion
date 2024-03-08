package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import java.util.Map;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Service
@RequiredArgsConstructor
public class Amambay implements Gatherer {

    private static final String URL_CHANGE = "https://www.bancobasa.com.py/ebanking_ext/api/data/currency_exchange";
    private static final String CODE = "AMAMBAY";

    private final HTTPHelper httpHelper;

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public List<QueryResponse> doQuery(Place p, List<PlaceBranch> branches) {

        try {

            QueryResponse qr = new QueryResponse(p);

            BranchExchangeData data = buildMapper().readValue(httpHelper.doGet(
                    URL_CHANGE,
                    5000,
                    Map.of(),
                    true
            ), BranchExchangeData.class);

            data.getCurrencyExchanges().forEach(exchange -> qr.addDetail(exchange.map()));

            return Collections.singletonList(qr);

        } catch (IOException e) {
            throw new AppException(500, "cant read response from Amambay ", e);
        }

    }

    private ObjectMapper buildMapper() {
        return new ObjectMapper();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BranchExchangeData {
        private String lastModified;
        private List<BranchExchangeDetailsData> currencyExchanges;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BranchExchangeDetailsData {
        private String isoCode;
        private String updateTs;
        private long purchasePrice;
        private long salePrice;

        private QueryResponseDetail map() {
            return new QueryResponseDetail(purchasePrice, salePrice, isoCode);
        }
    }
}

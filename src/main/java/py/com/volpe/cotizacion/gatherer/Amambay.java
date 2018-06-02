package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class Amambay implements Gatherer {

    private static final String URL_CHANGE = "https://www.bancoamambay.com.py/ebanking_ext/api/data/currency_exchange";
    private static final String CODE = "AMAMBAY";

    private final HTTPHelper httpHelper;

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public List<QueryResponse> doQuery(Place p, List<PlaceBranch> branches) {
        return branches.stream().map(this::queryBranch).collect(Collectors.toList());
    }

    private QueryResponse queryBranch(PlaceBranch branch) {
        try {

            QueryResponse qr = new QueryResponse(branch);

            BranchExchangeData data = buildMapper().readValue(httpHelper.doGet(URL_CHANGE), BranchExchangeData.class);

            data.getCurrencyExchanges().forEach(exchange -> qr.addDetail(exchange.map()));

            return qr;

        } catch (IOException e) {
            throw new AppException(500, "cant read response from chaco branch: " + branch.getId(), e);
        }
    }

    @Override
    public Place build() {

        Place place = new Place("Cambios Amambay", CODE);

        PlaceBranch centralBranch = PlaceBranch.builder().place(place).name("Central").build();

        place.addBranch(centralBranch);

        return place;
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

        QueryResponseDetail map() {
            return new QueryResponseDetail(purchasePrice, salePrice, isoCode);
        }
    }
}

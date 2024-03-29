package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class Alberdi implements Gatherer {

    private static final String CODE = "ALBERDI_2";
    private static final String WS_URL = "https://www.cambiosalberdi.com/ws/getTablero.json.php";
    private final HTTPHelper helper;

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public List<QueryResponse> doQuery(Place p, List<PlaceBranch> branches) {

        Map<String, List<ExchangeData>> result = getParsedData();

        return branches.stream().map(branch -> {

            QueryResponse qr = new QueryResponse(branch);
            List<ExchangeData> exchanges = result.get(branch.getRemoteCode());

            if (exchanges == null) {
                log.warn("No data for branch {} in {}", branch.getRemoteCode(), getCode());
                return qr;
            }

            exchanges.forEach(exchange -> {

                String iso = mapToISO(exchange);
                if (iso != null)
                    qr.addDetail(
                            new QueryResponseDetail(
                                    parse(exchange.getCompra()).longValue(),
                                    parse(exchange.getVenta()).longValue(),
                                    iso));
            });

            return qr;
        }).toList();


    }

    private BigDecimal parse(String number) {
        return new BigDecimal(number.replace(".", "").replace(",", "."));
    }

    protected Map<String, List<ExchangeData>> getParsedData() {
        try {
            String queryResult = helper.doGet(WS_URL);
            return buildMapper()
                    .readValue(queryResult, new TypeReference<>() {
                    });
        } catch (IOException e) {
            throw new AppException(500, "cant parse the result of alberdi ws", e);
        }
    }


    protected ObjectMapper buildMapper() {
        return new ObjectMapper();
    }


    private static String mapToISO(ExchangeData data) {
        // We don't care for the check exchange
        if (data.getMoneda().contains("Cheque")) return null;
        // we don't care about other exchanges
        if (data.getBcp().contains("->")) return null;
        if ("timer".equals(data.getId())) return null;
        return data.getBcp();
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ExchangeData {
        private String id;
        private String bcp;
        private String moneda;
        private String imagen;
        private String imaweb;
        private String compra;
        private String venta;
    }

}

package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.*;

/**
 * @author Arturo Volpe
 * @since 2021-01-02
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class EuroCambios implements Gatherer {

    protected static final String EXCHANGE_DATA_URL = "https://eurocambios.com.py/v2/sgi/utilsDto.php";

    private final HTTPHelper helper;

    /**
     * EuroCambios has two exchanges, one for the main and another for the secondary branches.
     */
    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        List<QueryResponseDetail> main = getExchangeOf("1");
        List<QueryResponseDetail> secondary = getExchangeOf("2");


        return branches.stream().map(branch -> {

                    QueryResponse qr = new QueryResponse(branch);
                    if (branch.getName().contains("Sucursal")) {
                        qr.setDetails(secondary.stream().map(QueryResponseDetail::dup).toList());
                    } else {
                        qr.setDetails(main.stream().map(QueryResponseDetail::dup).toList());
                    }
                    if (qr.getDetails().isEmpty()) return null;
                    return qr;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<QueryResponseDetail> getExchangeOf(String id) {

        Map<String, String> params = new HashMap<>();
        params.put("param", "getCotizacionesbySucursal");
        params.put("sucursal", id);
        String page = helper.doPost(EXCHANGE_DATA_URL, params);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode data;
        try {
            data = mapper.readTree(page);
        } catch (JsonProcessingException e) {
            log.warn("Can't parse data of {} of branch {}", getCode(), id, e);
            return Collections.emptyList();
        }
        List<QueryResponseDetail> toRet = new ArrayList<>();

        data.forEach(node -> {
            String currency = mapToISO(node.get("id").asText());
            if (currency == null) return;
            var purchase = parseLong(node.get("compra").asText());
            var sell = parseLong(node.get("venta").asText());
            var lastPurchase = parseLong(node.get("compra_anterior").asText());
            var lastSell = parseLong(node.get("venta_anterior").asText());

            QueryResponseDetail toAdd = new QueryResponseDetail(
                    purchase,
                    sell,
                    currency
            );
            toAdd.setPurchaseTrend(Long.compare(purchase, lastPurchase));
            toAdd.setSaleTrend(Long.compare(sell, lastSell));
            toRet.add(toAdd);
        });
        return toRet;
    }

    /**
     * Parses a string in the format `9400.00` as 9400L
     *
     * @param asStr the string to parse
     * @return the long value
     */
    private long parseLong(String asStr) {
        try {
            double asDbl = Double.parseDouble(asStr);
            return Math.round(asDbl);
        } catch (NumberFormatException e) {
            log.warn("Can't parse value {}", asStr, e);
            return 0L;
        }
    }

    private String mapToISO(String id) {
        if (id == null) return null;
        return switch (id) {
            case "US" -> "USD";
            case "EU" -> "EUR";
            case "PA" -> "ARS";
            case "RS" -> "BRL";
            case "PC" -> "CLP";
            case "PU" -> "UYU";
            case "YE" -> "JPY";
            case "DC" -> "CAD";
            case "FS" -> "CHF";
            case "LE" -> "GBP";
            default -> null;
        };
    }

    @Override
    public String getCode() {
        return "EURO";
    }
}

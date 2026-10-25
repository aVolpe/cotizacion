package py.com.volpe.cotizacion.gatherer;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.domain.Quote;

import java.math.BigDecimal;
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
        } catch (JacksonException e) {
            log.warn("Can't parse data of {} of branch {}", getCode(), id, e);
            return Collections.emptyList();
        }
        List<QueryResponseDetail> toRet = new ArrayList<>();

        data.forEach(node -> {
            String currency = mapToISO(node.get("id").asString());
            if (currency == null) return;
            var purchase = parse(node.get("compra").asString());
            var sell = parse(node.get("venta").asString());
            var lastPurchase = parse(node.get("compra_anterior").asString());
            var lastSell = parse(node.get("venta_anterior").asString());

            QueryResponseDetail toAdd = new QueryResponseDetail(
                    Quote.of(currency, purchase),
                    Quote.of(currency, sell)
            );
            toAdd.setPurchaseTrend(purchase.compareTo(lastPurchase));
            toAdd.setSaleTrend(sell.compareTo(lastSell));
            toRet.add(toAdd);
        });
        return toRet;
    }

    /**
     * Parses a string in the format `9400.00` as 9400.00 and `4.20` as 4.20
     *
     * @param asStr the string to parse
     * @return the price
     */
    private BigDecimal parse(String asStr) {
        try {
            return new BigDecimal(asStr.trim());
        } catch (NumberFormatException e) {
            log.warn("Can't parse value {}", asStr, e);
            return BigDecimal.ZERO;
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

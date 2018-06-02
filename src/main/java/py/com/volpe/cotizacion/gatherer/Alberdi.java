package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.WSHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class Alberdi implements Gatherer {

    private static final String CODE = "ALBERDI";
    private static final String WS_URL = "ws://cambiosalberdi.com:9300";
    private final WSHelper helper;

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

            exchanges.forEach(exchange -> {

                String iso = mapToISO(exchange);
                if (iso != null)
                    qr.addDetail(
                            new QueryResponseDetail(
                                    parse(exchange.getVenta()),
                                    parse(exchange.getCompra()),
                                    iso));
            });

            return qr;
        }).collect(Collectors.toList());


    }

    private long parse(String number) {
        return Long.parseLong(number.replace(".", ""));
    }

    /**
     * We read a file from the class path called "alberdi_branch_data", the file has the
     * important information about all the branches
     *
     * @return the place
     */
    @Override
    public Place build() {


        log.info("Creating place alberdi");

        Place place = new Place("Cambios Alberdi", CODE);

        Map<String, PlaceBranch> result = getBranchData();

        place.setBranches(new ArrayList<>(result.values()));

        return place;

    }

    private Map<String, PlaceBranch> getBranchData() {

        InputStream fileIS = getClass().getClassLoader().getResourceAsStream("alberdi_branch_data.json");

        try {
            return buildMapper()
                    .readValue(fileIS, new TypeReference<Map<String, PlaceBranch>>() {
                    });
        } catch (IOException e) {
            throw new AppException(500, "Can't read branch file", e);
        }
    }

    protected Map<String, List<ExchangeData>> getParsedData() {
        try {
            String queryResult = helper.getDataWithoutSending(WS_URL);
            return buildMapper()
                    .readValue(queryResult, new TypeReference<Map<String, List<ExchangeData>>>() {
                    });
        } catch (IOException e) {
            throw new AppException(500, "cant parse the result of alberdi ws", e);
        }
    }


    private ObjectMapper buildMapper() {
        return new ObjectMapper();
    }


    private static String mapToISO(ExchangeData data) {
        // We don't care for the check exchange
        if (data.getMoneda().contains("Cheque")) return null;
        switch (data.getImg()) {
            case "dolar.png":
                return "USD";
            case "real.png":
                return "BRL";
            case "euro.png":
                return "EUR";
            case "peso.png":
                return "ARS";
            default:
                return null;
        }
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ExchangeData {
        private String moneda;
        private String img;
        private String compra;
        private String venta;
    }

}

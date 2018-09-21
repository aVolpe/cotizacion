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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CambiosChaco implements Gatherer {

    private static final String URL_CHANGE = "http://www.cambioschaco.com.py/api/branch_office/%s/exchange";
    private static final String URL_BRANCH = "http://www.cambioschaco.com.py/api/branch_office/";
    public static final String CODE = "CAMBIOS_CHACO";

    private final HTTPHelper httpHelper;

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        return branches.stream().map(this::queryBranch).collect(Collectors.toList());
    }

    private QueryResponse queryBranch(PlaceBranch branch) {
        try {

            String query = httpHelper.doGet(String.format(URL_CHANGE, branch.getRemoteCode()));

            QueryResponse qr = new QueryResponse(branch);

            // for some reason, when cambios chaco doesn't has data, it returns '[]'
            if (!"[]".equals(query)) {
                BranchExchangeData data = buildMapper().readValue(query, BranchExchangeData.class);
                data.getItems()
                        .forEach(d -> {
                            if (d.getPurchasePrice() > 0) {
                                qr.addDetail(d.map());
                            }
                        });
            }

            return qr;

        } catch (IOException e) {
            throw new AppException(500, "cant read response from chaco branch: " + branch.getId(), e);
        }
    }

    @Override
    public String getCode() {
        return CODE;
    }

    public Place build() {
        log.info("Creating 'Cambios Chaco'");
        Place p = new Place("Cambios Chaco", CODE);

        try {
            List<BranchData> res = buildMapper().readValue(httpHelper.doGet(URL_BRANCH), new TypeReference<List<BranchData>>() {
            });

            p.setBranches(res.stream().map(d -> d.map(p)).collect(Collectors.toList()));

            return p;
        } catch (IOException e) {
            throw new AppException(500, "Can't read the body to build the place (chaco)", e);
        }
    }

    private ObjectMapper buildMapper() {
        return new ObjectMapper();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BranchExchangeData {
        private String updateTs;
        private String branchOfficeId;
        private List<BranchExchangeDetailsData> items;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BranchExchangeDetailsData {
        private String isoCode;
        private long purchasePrice;
        private long salePrice;
        private long purchaseTrend;
        private long saleTrend;

        public QueryResponseDetail map() {
            QueryResponseDetail toRet = new QueryResponseDetail();
            toRet.setIsoCode(isoCode);
            toRet.setPurchasePrice(purchasePrice);
            toRet.setSalePrice(salePrice);
            toRet.setPurchaseTrend(purchaseTrend);
            toRet.setSaleTrend(saleTrend);
            return toRet;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BranchData {
        private String id;
        private String title;
        private String lat;
        private String lng;
        private String phoneNumber;
        private String address;
        private String weekdaySchedule;
        private String saturdaySchedule;
        private String sundaySchedule;
        private String imageUrl;
        private String email;

        public PlaceBranch map(Place place) {
            PlaceBranch pb = new PlaceBranch();
            pb.setRemoteCode(id);
            pb.setPlace(place);
            pb.setEmail(email);
            pb.setName(title);
            pb.setImage(imageUrl);
            pb.setLatitude(Double.valueOf(lat));
            pb.setLongitude(Double.valueOf(lng));
            pb.setPhoneNumber(phoneNumber);
            pb.setSchedule(String.format("Semana: %s, Sabado: %s, Domingo: %s", weekdaySchedule, saturdaySchedule, sundaySchedule));
            return pb;
        }
    }
}

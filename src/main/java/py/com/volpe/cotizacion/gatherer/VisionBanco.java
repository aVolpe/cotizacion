package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 9/21/18
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class VisionBanco implements Gatherer {


    private static final String BRANCH_URL = "https://www.visionbanco.com/sucursales/list_items";
    private static final String EXCHANGE_URL = "https://www.visionbanco.com/";


    private final HTTPHelper helper;


    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        QueryResponse qr = new QueryResponse(place);

        String html = helper.doGet(EXCHANGE_URL);

        Document doc = Jsoup.parse(html);

        Element tables = doc.getElementById("efectivo");
        Elements exchanges = tables.select("tr");


        for (Element e : exchanges) {
            Elements columns = e.select("td");
            qr.addDetail(new QueryResponseDetail(
                    parse(columns.get(1).text()),
                    parse(columns.get(2).text()),
                    getISOName(columns.get(0).text().trim())
            ));
        }

        return Collections.singletonList(qr);

    }

    @Override
    public Place build() {

        try {

            String data = helper.doGet(BRANCH_URL);

            VisionBancoModel.VisionBranches sourceBranches = new ObjectMapper().readValue(data, VisionBancoModel.VisionBranches.class);


            Place p = Place.builder()
                    .code(getCode())
                    .type(Place.Type.BANK)
                    .name("Banco Visión")
                    .build();

            List<PlaceBranch> branches = sourceBranches.getItems()
                    .stream()
                    .filter(this::hasExchange)
                    .map(this::buildBranch)
                    .collect(Collectors.toList());

            p.setBranches(branches);

            return p;

        } catch (IOException e) {
            throw new AppException(500, "Can't build " + getCode(), e);
        }

    }

    private PlaceBranch buildBranch(VisionBancoModel.Item item) {

        String phone = "";
        if (!CollectionUtils.isEmpty(item.getContact())) {
            VisionBancoModel.Contact first = item.getContact().get(0);
            phone = first.getNumber();
        }

        StringBuilder schedule = new StringBuilder();
        if (!CollectionUtils.isEmpty(item.getHours())) {
            for (VisionBancoModel.Hour hour : item.getHours()) {
                schedule.append(hour.getDays().toValue())
                        .append(": ")
                        .append(hour.getOpen())
                        .append(" - ")
                        .append(hour.getClose())
                        .append("\n");
            }
        }

        return PlaceBranch
                .builder()
                .name(item.getTitle())
                .remoteCode(item.getUrlTitle())
                .phoneNumber(phone)
                .schedule(schedule.toString())
                .longitude(item.getGmap().getLongitude())
                .latitude(item.getGmap().getLatitude())
                .build();
    }

    private boolean hasExchange(VisionBancoModel.Item item) {
        if (item == null || item.getServices() == null || item.getServices().isEmpty()) {
            return false;
        }

        for (VisionBancoModel.Service service : item.getServices()) {
            if (service.getIcon() == VisionBancoModel.Icon.TIMER
                    || service.getIcon() == VisionBancoModel.Icon.SAC) {
                return true;
            }
        }
        return false;
    }

    private static String getISOName(String name) {
        switch (name) {
            case "DOLARES":
                return "USD";
            case "REAL BRASILEÑO":
                return "BRL";
            case "PESO ARGENTINO":
                return "ARS";
            case "EUROS":
                return "EUR";
            default:
                log.warn("Currency not mapped {}, returning null", name);
                return null;

        }

    }

    private static Long parse(String s) {
        return Long.parseLong(s
                .replace("compra", "")
                .replace("venta", "")
                .trim()
                .replaceAll("\\.", ""));
    }

    @Override
    public String getCode() {
        return "VISION_BANCO";
    }
}

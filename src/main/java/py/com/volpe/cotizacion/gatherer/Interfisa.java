package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author Arturo Volpe
 * @since 8/31/18
 */
@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class Interfisa implements Gatherer {

    private final HTTPHelper helper;
    private static final String WS_BRANCHES = "https://www.interfisa.com.py/sucursales.php";
    private static final String WS_EXCHANGE = "https://www.interfisa.com.py/";

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        Map<String, Pair<String, String>> data = getParsedData();

        return branches.stream().map(branch -> {

            QueryResponse qr = new QueryResponse(branch);

            data.forEach((key, value) -> {

                String iso = mapToISO(key);
                if (iso == null) {
                    return;
                }
                qr.addDetail(
                        new QueryResponseDetail(
                                parse(value.getFirst()),
                                parse(value.getSecond()),
                                iso));
            });

            return qr;
        }).collect(Collectors.toList());
    }

    @Override
    public Place build() {

        Place p = Place.builder()
                .name("Banco Interfisa")
                .code(getCode())
                .type(Place.Type.BANK)
                .build();

        p.setBranches(buildBranches());

        return p;
    }

    private List<PlaceBranch> buildBranches() {

        log.info("{} calling {}", getCode(), WS_BRANCHES);
        Document doc = Jsoup.parse(helper.doGet(WS_BRANCHES));
        List<PlaceBranch> data = new ArrayList<>();

        String hoursPrincipal = "Lunes a Viernes de 08:00 a 17:00 hs";
        String hoursSecondary = "Lunes a Viernes de 08:00 a 17:00 hs. Sabado de 08:00 a 11:30 hs.";

        Elements branches = doc.select(".acordeon > li");

        log.info("Found {} branches", branches.size());


        for (Element branch : branches) {
            String type = branch.parent().previousElementSibling().text();

            String title = branch.select("h3").text();
            String body = branch.select(".desplegable > p").text();


            PlaceBranch pb = new PlaceBranch();
            Pair<String, String> branchData = extractData(body);
            pb.setName(title);
            pb.setRemoteCode(title);

            Pair<Double, Double> location = parseLocation(branch.select("a").attr("rel"));

            if (location != null) {
                pb.setLatitude(location.getFirst());
                pb.setLongitude(location.getSecond());
            }

            if (type.startsWith("Interior")) {
                pb.setSchedule(hoursSecondary);
            } else {
                pb.setSchedule(hoursPrincipal);
            }

            pb.setPhoneNumber(branchData.getSecond());

            data.add(pb);
        }

        return data;

    }

    protected Pair<String, String> extractData(String text) {
        Pattern pattern = Pattern.compile("^.*:(.*) tel\\.:(.*)$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches() || matcher.groupCount() != 2) {
            throw new AppException(500, "The INTERFISA data '" + text + "' can't be parsed");
        }

        return Pair.of(
                matcher.group(1).trim().replaceAll("Dirección: ", ""),
                matcher.group(2).trim().replace("((", "("));

    }

    protected Pair<Double, Double> parseLocation(String location) {
        if (location.indexOf(',') < 0) {
            return null;
        }
        String[] parts = location.split(",");
        if (parts.length != 2) {
            return null;
        }
        return Pair.of(
                Double.parseDouble(parts[0].trim()),
                Double.parseDouble(parts[1].trim())
        );
    }

    protected static String mapToISO(String key) {
        // We don't care for the check exchange
        switch (key) {
            case "Dólar":
                return "USD";
            case "Euro":
                return "EUR";
            case "Peso":
                return "ARS";
            case "Real":
                return "BRL";
            default:
                return null;
        }
    }

    private Map<String, Pair<String, String>> getParsedData() {

        Map<String, Pair<String, String>> data = new HashMap<>();
        Document doc = Jsoup.parse(helper.doGet(WS_EXCHANGE));

        Elements rows = doc.select("#cotizaciones tr");

        for (Element branch : rows) {
            Elements columns = branch.select("td");
            if (columns.size() != 3) {
                continue;
            }

            data.put(columns.get(0).text(), Pair.of(columns.get(1).text(), columns.get(2).text()));
        }
        return data;
    }

    private long parse(String number) {
        return Long.parseLong(number.replace(".", ""));
    }

    @Override
    public String getCode() {
        return "INTERFISA";
    }
}

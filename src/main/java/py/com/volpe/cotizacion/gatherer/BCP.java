package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.Place.Type;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Arturo Volpe
 * @since 10/28/20
 */
@Service
@RequiredArgsConstructor
public class BCP implements Gatherer {

    private final HTTPHelper helper;

    private static final String BRANCH_DATA_URL = "https://www.bcp.gov.py/webapps/web/cotizacion/monedas";

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {
        PlaceBranch main = branches.get(0);
        return Collections.singletonList(query(main));
    }

    private QueryResponse query(PlaceBranch main) {

        QueryResponse toRet = new QueryResponse(main);

        try {
            String response = helper.doGet(BRANCH_DATA_URL, 20000, Collections.singletonMap(HttpHeaders.USER_AGENT, "Chrome"), true);

            Document root = Jsoup.parse(response);
            Elements exchangeDat = root.select("tbody tr");

            exchangeDat.stream()
                    .map(this::scrapeData)
                    .filter(Objects::nonNull)
                    .forEach(toRet::addDetail);

        } catch (IOException e) {
            throw new AppException(500, "cant read response from BCP", e);
        }

        return toRet;
    }

    private QueryResponseDetail scrapeData(Element element) {

        Elements columns = element.select("td");

        // this is the header
        if (columns.isEmpty()) return null;

        // String name = columns.first().text();
        String currency = columns.get(1).text();
        String rate = columns.get(3).text();

        return new QueryResponseDetail(parseAmount(rate), parseAmount(rate), currency);
    }

    @Override
    public Place build() {
        Place p = Place.builder()
                .name("Banco Central del Paraguay (Cotización referencial)")
                .type(Type.BANK)
                .code(getCode())
                .build();

        PlaceBranch pb = PlaceBranch.builder()
                .name("Central")
                .latitude(-25.2781319)
                .longitude(-57.5765498)
                .phoneNumber("+59521608011")
                .email("nfo@bcp.gov.py ")
                .remoteCode("01")
                .schedule("")
                .image("https://www.bcp.gov.py/userfiles/images/banners/slider-background-01.jpg")
                .place(p)
                .build();

        p.setBranches(Collections.singletonList(pb));
        return p;
    }

    @Override
    public String getCode() {
        return "BCP";
    }


    protected Long parseAmount(String amount) {
        return Long.parseLong(amount.substring(0, amount.indexOf(','))
                .replace(".", ""));

    }
}

package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.Place.Type;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 2021-01-02
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class Mundial implements Gatherer {

    private final HTTPHelper helper;
    private static final String BRANCH_LIST_URL = "https://www.mundialcambios.com.py/";
    private static final String BRANCH_INFO = "https://www.mundialcambios.com.py/?branch=%s";


    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        log.info("Calling a total of {} branches", branches.size());
        return branches.stream()
                .map(this::queryBranch)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private QueryResponse queryBranch(PlaceBranch branch) {
        try {

            log.info("Calling branch {}", branch.getRemoteCode());

            QueryResponse qr = new QueryResponse(branch);

            Document doc = Jsoup.parse(helper.doGet(String.format(BRANCH_INFO, branch.getRemoteCode()), 5000));
            Elements cards = doc.select(".card");
            for (Element card : cards) {

                Elements e = card.select(".titulo-cambio");
                if (e.first() == null || !e.first().text().contains("CAMBIO DEL D")) continue;

                Elements rows = card.select(".w-row");
                for (Element row : rows) {
                    String title = row.select(".titulo-divisa").text();
                    Elements exchanges = row.select(".divisa");
                    if (title.isEmpty() || exchanges.size() != 2) continue;
                    String buy = exchanges.get(0).text();
                    String sell = exchanges.get(1).text();
                    if (parse(buy) > 0 && parse(sell) > 0)
                        qr.addDetail(new QueryResponseDetail(parse(buy), parse(sell), title));
                }
                return qr;
            }

            return null;
        } catch (SocketTimeoutException se) {
            log.warn("calling the branch {} (remote {}) resulted in a timeout after 5000 milliseconds",
                    branch.getId(), branch.getRemoteCode());
            return null;
        }
    }

    @Override
    public Place build() {

        Place p = Place.builder()
                .name("MUNDIAL CAMBIOS")
                .code(getCode())
                .type(Type.BUREAU)
                .build();

        p.setBranches(buildBranches());

        return p;
    }

    private List<PlaceBranch> buildBranches() {
        log.info("{} calling {}", getCode(), BRANCH_LIST_URL);

        Document doc = Jsoup.parse(helper.doGet(BRANCH_LIST_URL));
        List<PlaceBranch> data = new ArrayList<>();

        Elements branches = doc.select(".sucursales-list > .w-dyn-item");

        log.info("Found {} branches", branches.size());


        for (Element branch : branches) {

            String title = branch.select("div").text();
            String href = branch.select("a").attr("href");
            if (!href.contains("=")) {
                throw new IllegalStateException("Invalid url for parsing: " + href);

            }
            String remoteId = href.substring(href.indexOf('=') + 1);

            log.info("{} calling {} to get info of branch", getCode(), href);
            Document branchDoc = Jsoup.parse(helper.doGet(href));


            PlaceBranch pb = new PlaceBranch();
            pb.setName(title);
            pb.setRemoteCode(remoteId);

            Pair<Double, Double> location = findLocation(branchDoc);
            String schedule = findSchedule(branchDoc);
            String phoneNumber = findPhone(branchDoc);

            if (location != null) {
                pb.setLatitude(location.getFirst());
                pb.setLongitude(location.getSecond());
            }

            if (phoneNumber != null)
                pb.setPhoneNumber(phoneNumber);
            if (schedule != null)
                pb.setSchedule(schedule);

            data.add(pb);
        }

        return data;
    }

    private String findPhone(Document branchDoc) {
        Elements infos = branchDoc.select(".info-sucursal-2");
        for (Element info : infos) {
            if (info.text().contains("595")) {
                return info.text();
            }
        }
        return null;
    }

    private String findSchedule(Document branchDoc) {
        Elements infos = branchDoc.select(".info-sucursal-2");
        for (Element info : infos) {
            if (info.text().contains("hs.")) {
                return info.text();
            }
        }
        return null;
    }

    private Pair<Double, Double> findLocation(Document branchDoc) {

        // <iframe width="100%" height="100%" frameborder="0" style="border:0" allowfullscreen src="https://www.google.com/maps/embed/v1/place?q=Mundial+Cambios&amp;center=-25.282219,-57.637459&amp;key=AIzaSyAkfCSZ82megwN9QIeDnGzH2v83MpBwRIY&amp;zoom=15"></iframe>
        Elements iframes = branchDoc.select("iframe");
        for (Element iframe : iframes) {
            try {
                String src = iframe.attr("src");
                if (src != null && src.contains("www.google.com")) {
                    MultiValueMap<String, String> parameters =
                            UriComponentsBuilder.fromUriString(src).build().getQueryParams();
                    String pair = parameters.getFirst("center");
                    if (pair != null) {
                        String[] parts = pair.split(",");
                        if (parts.length != 2) break;
                        return Pair.of(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                    }

                }
            } catch (Exception e) {
                log.warn("Invalid url found finding location", e);
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return "MUNDIAL";
    }

    protected static Long parse(String s) {
        return Long.parseLong(s.replace(".", ""));
    }
}

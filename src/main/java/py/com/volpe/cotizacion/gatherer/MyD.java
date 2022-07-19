package py.com.volpe.cotizacion.gatherer;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class MyD implements Gatherer {

    private static final String CODE = "MyD";
    private static final List<String> ASU_CODE = Arrays.asList("2", "3");
    private static final String URL = "https://www.mydcambios.com.py/";

    private final HTTPHelper helper;

    @Override
    public List<QueryResponse> doQuery(Place p, List<PlaceBranch> branches) {

        String query = helper.doGet(URL);

        return branches.stream().map(branch -> {

                    QueryResponse qr = new QueryResponse(branch);

                    int dataIndex = ASU_CODE.contains(branch.getRemoteCode()) ? 0 : 1;

                    List<ExchangeData> fullTable = getData(query, dataIndex);

                    fullTable.forEach(data -> {

                        String isoCode = getISOName(data);
                        Long purchase = parse(data.getPurchasePrice());
                        Long sale = parse(data.getSalePrice());

                        if (isoCode == null || purchase < 1 || sale < 1) {
                            return;
                        }

                        qr.addDetail(new QueryResponseDetail(
                                parse(data.getPurchasePrice()),
                                parse(data.getSalePrice()),
                                isoCode));
                    });

                    return qr;

                })
                .filter(Objects::nonNull)
                .toList();

    }

    @Override
    public String getCode() {
        return CODE;
    }


    private static List<ExchangeData> getData(String html, int idx) {
        try {
            Document doc = Jsoup.parse(html);
            List<ExchangeData> data = new ArrayList<>();

            Elements tables = doc.select(".cambios-banner-text.scrollbox");
            Element mainTables = tables.get(idx);


            Elements rows = mainTables.select("ul");

            for (Element e : rows) {
                Elements columns = e.select("li");
                if (".".equals(columns.get(0).text())) {
                    continue;
                }
                data.add(new ExchangeData(
                        getCurrencyName(columns.get(0).getElementsByTag("img").get(0).attr("src")),
                        columns.get(2).text().trim(),
                        columns.get(1).text().trim()));
            }

            return data;

        } catch (Exception e) {
            throw new AppException(500, "Can't connect to to MyD page (branch: " + idx + ")", e);
        }


    }

    private static String getISOName(ExchangeData data) {
        return switch (data.getCurrency()) {
            case "DOLARES AMERICANOS", "us-1" -> "USD";
            case "REALES", "descarga", "br" -> "BRL";
            case "PESOS ARGENTINOS", "ar" -> "ARS";
            case "EUROS", "640px-Flag_of_Europe.svg" -> "EUR";
            case "YEN JAPONES", "jp[1]" -> "JPY";
            case "LIBRAS ESTERLINAS", "260px-Bandera-de-inglaterra-400x240" -> "GBP";
            case "FRANCO SUIZO", "175-suiza_400px[1]" -> "CHF";
            case "DOLAR CANADIENSE", "ca[2]" -> "CAD";
            case "PESO CHILENO", "cl[1]" -> "CLP";
            case "PESO URUGUAYO", "200px-Flag_of_Uruguay_(1828-1830).svg", "uy[1]" -> "UYU";
            default -> null;
        };

    }

    protected static String getCurrencyName(String img) {

        return img.substring(img.lastIndexOf('/') + 1, img.lastIndexOf('.'));
    }

    private static Long parse(String s) {
        return Long.parseLong(s.replaceAll(",", "").replaceAll("\\..*", ""));
    }

    @Value
    private static class ExchangeData {
        String currency;
        String salePrice;
        String purchasePrice;
    }
}

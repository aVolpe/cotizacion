package py.com.volpe.cotizacion.gatherer;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MaxiCambios implements Gatherer {

    private static final String CODE = "MaxiCambios";
    private static final String WS_URL_AS = "http://cotizext.maxicambios.com.py/maxicambios4.xml";
    private static final String WS_URL_CDE = "http://cotizext.maxicambios.com.py/cotiza_cde.xml";

    private final HTTPHelper helper;

    @Override
    public List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches) {

        return branches.stream().map(this::queryBranch).collect(Collectors.toList());

    }

    private QueryResponse queryBranch(PlaceBranch branch) {

        QueryResponse qr = new QueryResponse(branch);

        getParsedData(getURLForBranch(branch)).forEach(detail -> {

            String iso = mapToISO(detail);
            if (iso != null)
                qr.addDetail(mapToDetail(detail, iso));
        });

        return qr;
    }

    private String getURLForBranch(PlaceBranch branch) {
        return "0".equals(branch.getRemoteCode()) ? WS_URL_AS : WS_URL_CDE;
    }

    private QueryResponseDetail mapToDetail(ExchangeData detail, String iso) {
        QueryResponseDetail qrd = new QueryResponseDetail();
        qrd.setIsoCode(iso);
        qrd.setSalePrice(detail.getVenta());
        qrd.setSaleTrend("up.png".equals(detail.getVentaTendencia()) ? 1 : -1);
        qrd.setPurchasePrice(detail.getCompra());
        qrd.setPurchaseTrend("up.png".equals(detail.getCompraTendencia()) ? 1 : -1);
        return qrd;
    }

    @Override
    public String getCode() {
        return CODE;
    }


    /**
     * More branches are present in the main webpage, but it's seems that all the branches shares the same price.
     *
     * @return the newly created place
     */
    @Override
    public Place build() {


        log.info("Creating place {}", CODE);

        Place place = new Place();
        place.setName(CODE);
        place.setCode(CODE);


        PlaceBranch main = new PlaceBranch();
        main.setName("Shopping Multiplaza Casa Central");
        main.setLatitude(-25.3167006);
        main.setLongitude(-57.572267);
        main.setImage("http://www.maxicambios.com.py/media/1044/asuncion_multiplaza.jpg");
        main.setPhoneNumber("(021) 525105/8");
        main.setSchedule("Lunes a Sábados: 8:00 a 21:00 Hs.\n" +
                "Domingos: 10:00 a 21:00 Hs.");
        main.setRemoteCode("0");

        PlaceBranch cde = new PlaceBranch();
        cde.setName("Casa Central CDE");
        cde.setLatitude(-25.5083135);
        cde.setLongitude(-54.6384264);
        cde.setImage("http://www.maxicambios.com.py/media/1072/matriz_cde_original.jpg");
        cde.setPhoneNumber("(061) 573106-574270-574295-509511/13");
        cde.setSchedule("Lunes a viernes: 7:00 a 19:30 Hs. \n Sabados: 7:00 a 12:00 Hs.");
        cde.setRemoteCode("13");


        place.setBranches(Arrays.asList(main, cde));
        return place;

    }

    private List<ExchangeData> getParsedData(String wsUrl) {

        String xml = getData(wsUrl);
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        List<ExchangeData> toRet = new ArrayList<>();
        for (Element e : doc.select("cotizaciones > moneda")) {
            ExchangeData ed = new ExchangeData();
            ed.setPais(e.getElementsByTag("pais").text());
            ed.setTipo(e.getElementsByTag("tipo").text());
            if (!ed.getTipo().equals("EFECTIVO")) {
                continue;
            }
            ed.setNombre(e.getElementsByTag("nombre").text());
            ed.setBandera(e.getElementsByTag("bandera").text());
            ed.setCompra(parse(e.getElementsByTag("compra").text()));
            ed.setCompraTendencia(e.getElementsByTag("compra_tendencia").text());
            ed.setVenta(parse(e.getElementsByTag("venta").text()));
            ed.setVentaTendencia(e.getElementsByTag("venta_tendencia").text());
            System.out.println(ed);

            toRet.add(ed);
        }
        return toRet;
    }

    private String getData(String wsUrl) {

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String todayStr = sdf.format(new Date());
        return this.helper.doGet(String.format(wsUrl, todayStr));
    }

    private static String mapToISO(ExchangeData data) {
        if (!"EFECTIVO".equals(data.tipo)) {
            return null;
        }
        switch (data.pais) {
            case "EEUU":
                return "USD";
            case "Argentina":
                return "ARS";
            case "Brasil":
                return "BRL";
            case "Uruguay": //URU
                return "UYU";
            case "EU": //EURO
            case "Euro": //EURO
                return "EUR";
            case "Mejico": //Mexico
                return "MXN";
            case "Gran Bretaña": //Libra
                return "GBP";
            case "Japon": //Japon
                return "JPY";
            case "Chile":
                return "CLP";
            case "Bolivia":
                return "BOB";
            case "Colombia":
                return "COP";
            case "Peruano": //Peru
                return "PEN";
            case "Suecia":
                return "SEK";
            case "Dinamarca":
                return "DKK";
            case "Canadá":
                return "CAD";
            case "Australia":
                return "AUD";
            case "Suiza":
                return "CHF";
            default:
                if (data.nombre.equals("Libra")) return "GBP";
                if (data.pais.startsWith("Canad")) return "CAD";
                return null;
        }
    }

    /**
     * <pre>
     *     <cotizaciones>
     *      <moneda>
     *       <tipo>EFECTIVO</tipo>
     *       <pais>EEUU</pais>
     *       <nombre>Dólar</nombre>
     *       <bandera>us.png</bandera>
     *       <compra>6300</compra>
     *       <compra_tendencia>up.png</compra_tendencia>
     *       <venta>6380</venta>
     *       <venta_tendencia>up.png</venta_tendencia>
     *      </moneda>
     *      <actualizacion>04/01/2020 - 20:40</actualizacion>
     *     </cotizaciones>
     * </pre>
     */
    @Data
    private static class ExchangeData {
        private String tipo;
        private String pais;
        private String nombre;
        private String bandera;
        private long compra;
        private String compraTendencia;
        private long venta;
        private String ventaTendencia;
    }

    private static Long parse(String value) {
        return Long.parseLong(value.replaceAll("\\..*", ""));
    }

}


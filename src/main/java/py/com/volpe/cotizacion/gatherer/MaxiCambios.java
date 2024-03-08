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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        return branches.stream().map(this::queryBranch).toList();

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
        qrd.setSalePrice(detail.getVenta().longValue());
        qrd.setSaleTrend("up.png".equals(detail.getVentaTendencia()) ? 1 : -1);
        qrd.setPurchasePrice(detail.getCompra().longValue());
        qrd.setPurchaseTrend("up.png".equals(detail.getCompraTendencia()) ? 1 : -1);
        return qrd;
    }

    @Override
    public String getCode() {
        return CODE;
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
            case "EU", "Euro": //EURO
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
                if ("Libra".equals(data.getNombre())) return "GBP";
                if (data.getPais() != null && data.getPais().startsWith("Canad")) return "CAD";
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
        private BigDecimal compra;
        private String compraTendencia;
        private BigDecimal venta;
        private String ventaTendencia;
    }

    private static BigDecimal parse(String value) {
        return new BigDecimal(value
                .replaceAll("\\..*", "")
                .replaceAll(",", ".")
        );
    }

}


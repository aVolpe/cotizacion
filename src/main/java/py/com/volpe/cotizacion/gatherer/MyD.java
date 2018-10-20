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
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class MyD implements Gatherer {

	private static final String CODE = "MyD";
	private static final String ASU_CODE = "2";
	private static final String CDE_CODE = "4";
	private static final String URL = "https://www.mydcambios.com.py/";

	private final HTTPHelper helper;

	@Override
	public List<QueryResponse> doQuery(Place p, List<PlaceBranch> branches) {

		String query = helper.doGet(URL);

		return branches.stream().map(branch -> {

			if (!branch.getRemoteCode().equals(ASU_CODE) && !branch.getRemoteCode().equals(CDE_CODE)) {
				return null;
			}

			QueryResponse qr = new QueryResponse(branch);

			List<ExchangeData> fullTable = getData(query, branch.getRemoteCode().equals(ASU_CODE) ? 0 : 1);

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

		}).filter(Objects::nonNull).collect(Collectors.toList());

	}

	@Override
	public String getCode() {
		return CODE;
	}


	@Override
	public Place build() {


		log.info("Creating place MyD");
		Place place = new Place("Cambios MyD", CODE);

		PlaceBranch matriz = new PlaceBranch();
		matriz.setName("Casa Matriz");
		matriz.setRemoteCode("2");
		matriz.setPhoneNumber("021451377/9");
		matriz.setLatitude(-25.281474);
		matriz.setLongitude(-57.637259);
		matriz.setImage("https://www.mydcambios.com.py/uploads/sucursal/2/_principal_myd_cambios_matriz.jpg");


		PlaceBranch villa = new PlaceBranch();
		villa.setName("Villa Morra");
		villa.setRemoteCode("3");
		villa.setPhoneNumber("021-662537 /  021-609662");
		villa.setLatitude(-25.294182);
		villa.setLongitude(-57.579190);
		villa.setImage("https://www.mydcambios.com.py/uploads/sucursal/3/_principal_img_20160205_wa0007.jpg");

		PlaceBranch cde = new PlaceBranch();
		cde.setName("Agencia KM4 - Cotizaciones");
		cde.setRemoteCode("4");
		cde.setPhoneNumber("021-662537 /  021-609662");
		cde.setLatitude(-25.508799);
		cde.setLongitude(-54.639613);
		cde.setImage("https://www.mydcambios.com.py/uploads/sucursal/4/_principal_img_20160218_wa0060.jpg");

		place.setBranches(Arrays.asList(matriz, villa, cde));

		return place;

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
		switch (data.getCurrency()) {
			case "DOLARES AMERICANOS":
			case "us-1":
				return "USD";
			case "REALES":
			case "descarga":
			case "br":
				return "BRL";
			case "PESOS ARGENTINOS":
			case "ar":
				return "ARS";
			case "EUROS":
			case "640px-Flag_of_Europe.svg":
				return "EUR";
			case "YEN JAPONES":
			case "jp[1]":
				return "JPY";
			case "LIBRAS ESTERLINAS":
			case "260px-Bandera-de-inglaterra-400x240":
				return "GBP";
			case "FRANCO SUIZO":
			case "175-suiza_400px[1]":
				return "CHF";
			case "DOLAR CANADIENSE":
			case "ca[2]":
				return "CAD";
			case "PESO CHILENO":
			case "cl[1]":
				return "CLP";
			case "PESO URUGUAYO":
			case "200px-Flag_of_Uruguay_(1828-1830).svg":
			case "uy[1]":
				return "UYU";
			default:
				log.warn("Currency not mapped {}, returning null", data.getCurrency());
				return null;

		}

	}

	protected static String getCurrencyName(String img) {

		return img.substring(img.lastIndexOf('/') + 1, img.lastIndexOf('.'));
	}

	private static Long parse(String s) {
		return Long.parseLong(s.replaceAll(",", "").replaceAll("\\..*", ""));
	}

	@Value
	private static final class ExchangeData {
		private String currency;
		private String salePrice;
		private String purchasePrice;
	}
}

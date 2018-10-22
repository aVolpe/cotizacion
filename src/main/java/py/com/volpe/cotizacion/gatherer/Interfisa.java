package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

		Map<String, Pair<Long, Long>> data = getParsedData();


		QueryResponse qr = new QueryResponse(place);

		data.forEach((key, value) -> {

			String iso = mapToISO(key);
			if (iso == null) {
				return;
			}
			qr.addDetail(
					new QueryResponseDetail(
							value.getFirst(),
							value.getSecond(),
							iso));
		});

		return Collections.singletonList(qr);
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
			case "DOLARES AMERICANOS":
				return "USD";
			case "Euro":
			case "EUROS":
				return "EUR";
			case "Peso":
			case "PESO ARGENTINO":
				return "ARS";
			case "Real":
			case "REAL":
				return "BRL";
			default:
				return null;
		}
	}

	private Map<String, Pair<Long, Long>> getParsedData() {

		try {
			Map<String, Pair<Long, Long>> toRet = new HashMap<>();
			String json = helper.doGet(WS_EXCHANGE);

			ObjectMapper om = new ObjectMapper();
			JsonNode data = om.readTree(json);

			JsonNode currencyDataList = data.get("operacionResponse").get("cotizaciones").get("monedaCot");
			for (JsonNode currencyData : currencyDataList) {
				toRet.put(currencyData.get("descripcion").asText(),
						Pair.of(currencyData.get("compra").asLong(), currencyData.get("venta").asLong()));
			}


			return toRet;
		} catch (Exception e) {
			throw new AppException(500, "Can't parse interfisa data", e);
		}
	}

	@Override
	public String getCode() {
		return "INTERFISA";
	}

}

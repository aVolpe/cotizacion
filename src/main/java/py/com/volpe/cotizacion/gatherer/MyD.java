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
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.repository.PlaceRepository;
import py.com.volpe.cotizacion.repository.QueryResponseRepository;

import java.io.IOException;
import java.util.*;
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
	private static final String URL = "https://www.mydcambios.com.py/?sucursal=%s";
	private final PlaceRepository placeRepository;
	private final QueryResponseRepository queryResponseRepository;

	@Override
	public List<QueryResponse> doQuery() {

		Place p = addOrUpdatePlace();

		return p.getBranches().stream().map(branch -> {

			QueryResponse qr = new QueryResponse();
			qr.setPlace(p);
			qr.setBranch(branch);
			qr.setDate(new Date());
			qr.setDetails(new ArrayList<>());

			List<ExchangeData> fullTable = getData(branch.getRemoteCode());

			fullTable.forEach(data -> {
				QueryResponseDetail qrd = new QueryResponseDetail();
				qrd.setQueryResponse(qr);
				qrd.setIsoCode(getISOName(data));
				qrd.setPurchasePrice(parse(data.getPurchasePrice()));
				qrd.setSalePrice(parse(data.getSalePrice()));
				qr.getDetails().add(qrd);
			});

			return queryResponseRepository.save(qr);

		}).collect(Collectors.toList());

	}

	@Override
	public Optional<Place> get() {
		return placeRepository.findPlaceByCode(CODE);
	}

	@Override
	public Place addOrUpdatePlace() {
		return placeRepository.findPlaceByCode(CODE).orElseGet(this::create);
	}

	@Override
	public String getCode() {
		return CODE;
	}


	private Place create() {


		log.info("Creating place MyD");
		Place p = new Place();
		p.setName("Cambios MyD");
		p.setCode(CODE);

		PlaceBranch pb = new PlaceBranch();
		pb.setPlace(p);
		pb.setName("Casa Matriz");
		pb.setRemoteCode("2");
		pb.setPhoneNumber("021451377/9");
		pb.setLatitude(-25.281474);
		pb.setLongitude(-57.637259);
		pb.setImage("https://www.mydcambios.com.py/uploads/sucursal/2/_principal_myd_cambios_matriz.jpg");


		PlaceBranch villa = new PlaceBranch();
		villa.setPlace(p);
		villa.setName("Villa Morra");
		villa.setRemoteCode("3");
		villa.setPhoneNumber("021-662537 /  021-609662");
		villa.setLatitude(-25.294182);
		villa.setLongitude(-57.579190);
		villa.setImage("https://www.mydcambios.com.py/uploads/sucursal/3/_principal_img_20160205_wa0007.jpg");

		PlaceBranch cde = new PlaceBranch();
		cde.setPlace(p);
		cde.setName("Agencia KM4 - Cotizaciones");
		cde.setRemoteCode("4");
		cde.setPhoneNumber("021-662537 /  021-609662");
		cde.setLatitude(-25.508799);
		cde.setLongitude(-54.639613);
		cde.setImage("https://www.mydcambios.com.py/uploads/sucursal/4/_principal_img_20160218_wa0060.jpg");

		p.setBranches(Arrays.asList(pb, villa, cde));

		return placeRepository.save(p);

	}

	private static List<ExchangeData> getData(String sucId) {
		try {
			Document doc = Jsoup.connect(String.format(URL, sucId)).get();
			List<ExchangeData> data = new ArrayList<>();

			Elements tables = doc.select("#cotizaciones table");
			Element mainTables = tables.get(0);


			Elements rows = mainTables.select("tbody tr");

			for (Element e : rows) {
				Elements columns = e.select("td");
				data.add(new ExchangeData(
						columns.get(0).text().trim(),
						columns.get(2).text().trim(),
						columns.get(1).text().trim()));
			}

			return data;

		} catch (IOException e) {
			throw new AppException(500, "Can't connecto to MyD page");
		}


	}

	private static String getISOName(ExchangeData data) {
		switch (data.getCurrency()) {
			case "DOLARES AMERICANOS":
				return "USD";
			case "REALES":
				return "BRL";
			case "PESOS ARGENTINOS":
				return "ARS";
			case "EUROS":
				return "EUR";
			case "YEN JAPONES":
				return "JPY";
			case "LIBRAS ESTERLINAS":
				return "GBP";
			case "FRANCO SUIZO":
				return "CHF";
			case "DOLAR CANADIENSE":
				return "CAD";
			case "PESO CHILENO":
				return "CLP";
			case "PESO URUGUAYO":
				return "UYU";
			default:
				log.warn("Currency not mapped {}, returning null", data.getCurrency());
				return null;

		}

	}

	private static Long parse(String s) {
		return Long.parseLong(s.replaceAll(",", "").replaceAll("\\..*", ""));
	}

	@Value
	private static final class ExchangeData {
		String currency;
		String salePrice;
		String purchasePrice;
	}
}

package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.HTTPHelper;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.repository.PlaceRepository;
import py.com.volpe.cotizacion.repository.QueryResponseRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class Amambay implements Gatherer {

	private static final String URL_CHANGE = "https://www.bancoamambay.com.py/ebanking_ext/api/data/currency_exchange";
	private static final String CODE = "AMAMBAY";

	private final PlaceRepository placeRepository;
	private final QueryResponseRepository queryResponseRepository;
	private final HTTPHelper httpHelper;

	@Override
	public List<QueryResponse> doQuery() {


		log.info("Starting doing query in {}", CODE);
		Place p = addOrUpdatePlace();
		return p.getBranches().stream().map(this::queryBranch).collect(Collectors.toList());
	}

	private QueryResponse queryBranch(PlaceBranch branch) {
		try {

			QueryResponse qr = new QueryResponse();
			qr.setBranch(branch);
			qr.setDate(new Date());
			qr.setPlace(branch.getPlace());

			String query = httpHelper.doGet(URL_CHANGE);

			BranchExchangeData data = buildMapper().readValue(query, BranchExchangeData.class);

			qr.setDetails(data.getCurrencyExchanges().stream().map(i -> i.map(qr)).collect(Collectors.toList()));


			return queryResponseRepository.save(qr);
		} catch (IOException e) {
			throw new AppException(500, "cant read response from chaco branch: " + branch.getId(), e);
		}
	}

	@Override
	public Optional<Place> get() {
		return placeRepository.findPlaceByCode(CODE);
	}

	@Override
	public Place addOrUpdatePlace() {

		return get().orElseGet(this::create);
	}

	@Override
	public String getCode() {
		return CODE;
	}

	private Place create() {
		Place p = new Place();
		p.setName("Cambios Amambay");
		p.setCode(CODE);

		PlaceBranch pb = new PlaceBranch();
		pb.setPlace(p);
		pb.setName("Central");

		p.setBranches(Collections.singletonList(pb));

		return placeRepository.save(p);
	}

	private ObjectMapper buildMapper() {
		return new ObjectMapper();
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class BranchExchangeData {
		private String lastModified;
		private List<BranchExchangeDetailsData> currencyExchanges;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BranchExchangeDetailsData {
		private String isoCode;
		private String updateTs;
		private long purchasePrice;
		private long salePrice;

		QueryResponseDetail map(QueryResponse qr) {
			QueryResponseDetail toRet = new QueryResponseDetail();
			toRet.setIsoCode(isoCode);
			toRet.setQueryResponse(qr);
			toRet.setPurchasePrice(purchasePrice);
			toRet.setSalePrice(salePrice);
			return toRet;
		}
	}
}

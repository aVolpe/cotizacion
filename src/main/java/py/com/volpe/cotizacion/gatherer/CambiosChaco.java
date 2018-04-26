package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
public class CambiosChaco {

	public static final String URL_CHANGE = "http://www.cambioschaco.com.py/api/branch_office/%s/exchange";
	public static final String URL_BRANCH = "http://www.cambioschaco.com.py/api/branch_office/";
	public static final String CODE = "CAMBIOS_CHACO";

	private final PlaceRepository placeRepository;
	private final QueryResponseRepository queryResponseRepository;
	private final HTTPHelper httpHelper;

	public List<QueryResponse> doQuery() {


		Place p = addOrUpdatePlace();
		return p.getBranches().stream().map(this::queryBranch).collect(Collectors.toList());
	}

	private QueryResponse queryBranch(PlaceBranch branch) {
		try {

			String query = httpHelper.doGet(String.format(URL_CHANGE, branch.getRemoteCode()));

			QueryResponse qr = new QueryResponse();
			qr.setBranch(branch);
			qr.setDate(new Date());
			qr.setPlace(branch.getPlace());

			// for some reason, when cambios chaco doesn't has data, it returns '[]'
			if (!"[]".equals(query)) {
				BranchExchangeData data = buildMapper().readValue(query, BranchExchangeData.class);
				qr.setDetails(data.getItems().stream().map(d -> d.map(qr)).collect(Collectors.toList()));
			}

			return queryResponseRepository.save(qr);

		} catch (IOException e) {
			throw new AppException(500, "cant read response from chaco branch: " + branch.getId(), e);
		}
	}

	public Optional<Place> get() {
		return placeRepository.findPlaceByCode(CODE);
	}

	public Place addOrUpdatePlace() {

		return get().orElseGet(this::create);
	}

	private Place create() {
		Place p = new Place();
		p.setName("Cambios Chaco");
		p.setCode(CODE);

		try {
			List<BranchData> res = buildMapper().readValue(httpHelper.doGet(URL_BRANCH), new TypeReference<List<BranchData>>() {
			});

			p.setBranches(res.stream().map(d -> d.map(p)).collect(Collectors.toList()));

			return placeRepository.save(p);
		} catch (IOException e) {
			throw new AppException(500, "Can't read the body to build the place (chaco)", e);
		}
	}

	private ObjectMapper buildMapper() {
		return new ObjectMapper();
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BranchExchangeData {
		String updateTs;
		String branchOfficeId;
		List<BranchExchangeDetailsData> items;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BranchExchangeDetailsData {
		String isoCode;
		long purchasePrice;
		long salePrice;
		long purchaseTrend;
		long saleTrend;

		public QueryResponseDetail map(QueryResponse qr) {
			QueryResponseDetail toRet = new QueryResponseDetail();
			toRet.setIsoCode(isoCode);
			toRet.setQueryResponse(qr);
			toRet.setPurchasePrice(purchasePrice);
			toRet.setSalePrice(salePrice);
			toRet.setPurchaseTrend(purchaseTrend);
			toRet.setSaleTrend(saleTrend);
			return toRet;
		}
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BranchData {
		String id;
		String title;
		String lat;
		String lng;
		String phoneNumber;
		String address;
		String weekdaySchedule;
		String saturdaySchedule;
		String sundaySchedule;
		String imageUrl;
		String email;

		public PlaceBranch map(Place place) {
			PlaceBranch pb = new PlaceBranch();
			pb.setRemoteCode(id);
			pb.setPlace(place);
			pb.setEmail(email);
			pb.setName(title);
			pb.setImage(imageUrl);
			pb.setLatitude(Double.valueOf(lat));
			pb.setLongitude(Double.valueOf(lng));
			pb.setPhoneNumber(phoneNumber);
			pb.setSchedule(String.format("Semana: %s, Sabado: %s, Domingo: %s", weekdaySchedule, saturdaySchedule, sundaySchedule));
			return pb;
		}
	}
}

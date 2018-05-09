package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import py.com.volpe.cotizacion.AppException;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.repository.PlaceRepository;
import py.com.volpe.cotizacion.repository.QueryResponseRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class Alberdi implements Gatherer {

	private static final String CODE = "ALBERDI";
	private static final String WS_URL = "ws://cambiosalberdi.com:9300";
	private final PlaceRepository placeRepository;
	private final QueryResponseRepository queryResponseRepository;

	@Override
	public List<QueryResponse> doQuery() {

		Place p = addOrUpdatePlace();


		try {
			Map<String, List<ExchangeData>> result = getParsedData();

			return p.getBranches().stream().map(b -> {
				QueryResponse qr = new QueryResponse();
				qr.setBranch(b);
				qr.setDate(new Date());
				qr.setPlace(b.getPlace());
				List<ExchangeData> data = result.get(b.getRemoteCode());

				qr.setDetails(data.stream().map(detail -> {

					String iso = mapToISO(detail);
					if (iso == null) return null;

					QueryResponseDetail qrd = new QueryResponseDetail();
					qrd.setQueryResponse(qr);
					qrd.setIsoCode(iso);
					qrd.setSalePrice(Long.parseLong(detail.getVenta().replace(".", "")));
					qrd.setPurchasePrice(Long.parseLong(detail.getCompra().replace(".", "")));

					return qrd;
				}).filter(Objects::nonNull).collect(Collectors.toList()));


				return queryResponseRepository.save(qr);
			}).collect(Collectors.toList());


		} catch (IOException e) {
			throw new AppException(500, "cant parse the result of alberdi ws", e);
		}

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


		log.info("Creating place alberdi");
		Place p = new Place();
		p.setName("Cambios Alberdi");
		p.setCode(CODE);
		p.setBranches(new ArrayList<>());

		try {
			Map<String, List<ExchangeData>> result = getParsedData();

			result.keySet().forEach(name -> {
				PlaceBranch pb = new PlaceBranch();
				pb.setName(name);
				pb.setRemoteCode(name);
				pb.setPlace(p);
				addMetaData(pb);

				p.getBranches().add(pb);
			});

			return placeRepository.save(p);
		} catch (IOException e) {
			throw new AppException(500, "cant parse the result of alberdi ws", e);
		}

	}

	private void addMetaData(PlaceBranch pb) {
		switch (pb.getName()) {
			case "asuncion":
				pb.setName("Asunción");
				pb.setLatitude(-25.281411);
				pb.setLongitude(-57.6375917);
				pb.setImage("https://lh5.googleusercontent.com/p/AF1QipMxA2Nv-mtjAzqti1pUgd_Bt3z8nfbBBizklGEw=w408-h244-k-no");
				pb.setPhoneNumber("(021) 447.003 / (021) 447.004");
				pb.setSchedule("07:45 horas a 17:00 horas de Lunes a Viernes, 07:45 horas a 12:00 horas Sábados");
				pb.setEmail("matriz@cambiosalberdi.com");
				break;
			case "villamorra":
				pb.setName("Villa Morra");
				pb.setPhoneNumber("(021) 609.905 / (021) 609.906");
				pb.setSchedule("08:00 horas a 17:00 horas de Lunes a Viernes, 08:00 horas a 12:00 horas Sábados");
				pb.setImage("https://lh5.googleusercontent.com/p/AF1QipP9gq7gRfgXTFGdQFGJYWLZGq_9SSLJ_pKYN4Uk=w408-h306-k-no");
				pb.setLatitude(-25.2962143);
				pb.setLongitude(-57.5766948);
				break;
			case "sanlo":
				pb.setName("San Lorenzo");
				pb.setImage("https://lh5.googleusercontent.com/p/AF1QipM0yx3fvWQArt0kY6EwyaaAsgX1jYRy3OuIobjr=w408-h725-k-no");
				pb.setLatitude(-25.3459184);
				pb.setLongitude(-57.5151255);
				pb.setPhoneNumber("Teléfonos: (021) 571.215 / (021) 571.216");
				pb.setSchedule("08:00 horas a 17:00 horas de Lunes a Viernes, 08:00 horas a 12:00 horas Sábados");
				break;
			case "salto":
				pb.setName("SALTO DEL GUAIRÁ");
				pb.setLatitude(-24.055276);
				pb.setLongitude(-54.3246485);
				pb.setPhoneNumber("Teléfonos: (046) 243.158 / (046) 243.159");
				pb.setSchedule("08:00 horas a 16:00 horas de Lunes a Viernes, 07:30 horas a 11:30 horas Sábados");
				break;
			case "cde":
				pb.setName("SUCURSAL 1 CDE");
				pb.setLatitude(-25.5098204);
				pb.setLongitude(-54.6164127);
				pb.setPhoneNumber("Teléfonos: (061) 500.135 / (061) 500.417");
				pb.setSchedule("07:00 horas a 17:00 horas de Lunes a Viernes, 07:00 horas a 12:00 horas Sábados");
				break;
			case "cde2":
				pb.setName("CDE KM 4");
				pb.setLatitude(-25.5095271);
				pb.setLongitude(-54.6485326);
				pb.setPhoneNumber("Teléfonos: (061) 571.540 / (061) 571.536");
				pb.setSchedule("07:00 horas a 17:00 horas de Lunes a Viernes, 07:00 horas a 12:00 horas Sábados");
				break;
			case "enc":
				pb.setName("ENCARNACIÓN");
				pb.setLatitude(-27.3314553);
				pb.setLongitude(-55.8670186);
				pb.setImage("https://lh5.googleusercontent.com/p/AF1QipOAtjZef_kGv14qJ4h68Rt4CKOxxwYXPJW30BUY=w408-h306-k-no");
				pb.setSchedule("07:45 horas a 17:00 horas de Lunes a Viernes, 07:45 horas a 12:00 horas Sábados");
				pb.setPhoneNumber("Teléfonos: (071) 205.154 / (071) 205.120 / (071) 205.144");
				break;
			default:
				log.warn("Unkonw branch {} of alberdi", pb.getName());

		}
	}

	private Map<String, List<ExchangeData>> getParsedData() throws IOException {
		return buildMapper().readValue(getData(), new TypeReference<Map<String, List<ExchangeData>>>() {
		});
	}


	private ObjectMapper buildMapper() {
		return new ObjectMapper();
	}

	/**
	 * This method is overly complicated
	 * <p>
	 * First we create a {@link StandardWebSocketClient}, and attach it to a manager along with a
	 * handler, in this case an {@link AbstractWebSocketHandler} and we wait for the handleTextMessage for the
	 * real message.
	 * <p>
	 * Obviously this API is created to be used in more complex scenarios, in this case we only need one message,
	 * the message is automatically send (we don't need to ask for it).
	 * <p>
	 * The API is prepared to be used in an async environment, we make it 'sync' here using the wait/notify java
	 * API.
	 *
	 * @return the json that the web service return
	 */
	protected static String getData() {

		try {
			Object monitor = new Object();
			AtomicReference<String> val = new AtomicReference<>();

			StandardWebSocketClient swsc = new StandardWebSocketClient();
			WebSocketConnectionManager manager = new WebSocketConnectionManager(swsc, new AbstractWebSocketHandler() {
				@Override
				protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
					val.set(message.getPayload());
					session.close();
					synchronized (monitor) {
						monitor.notifyAll();
					}
				}
			}, WS_URL);
			manager.setAutoStartup(true);
			manager.start();
			int retries = 4;
			String result = null;
			while (val.get() == null && retries-- > 0) {
				synchronized (monitor) {
					monitor.wait(10000);
					result = val.get();
				}
			}
			manager.stop();
			return result;
		} catch (Exception e) {
			throw new AppException(500, "can't connect to socket alberdi", e);
		}
	}

	private static String mapToISO(ExchangeData data) {
		// We don't care for the check exchange
		if (data.moneda.contains("Cheque")) return null;
		switch (data.img) {
			case "dolar.png":
				return "USD";
			case "real.png":
				return "BRL";
			case "euro.png":
				return "EUR";
			case "peso.png":
				return "ARS";
			default:
				return null;
		}
	}

	@Data
	private static class ExchangeData {
		String moneda;
		String img;
		String compra;
		String venta;
	}

}

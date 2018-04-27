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
				p.getBranches().add(pb);
			});

			return placeRepository.save(p);
		} catch (IOException e) {
			throw new AppException(500, "cant parse the result of alberdi ws", e);
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
	private static String getData() {

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
		try {
			String result = null;
			while (val.get() == null) {
				synchronized (monitor) {
					monitor.wait(10000);
					result = val.get();
				}
			}
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

	public static void main(String... args) throws Exception {

		System.out.println(getData());
	}
}

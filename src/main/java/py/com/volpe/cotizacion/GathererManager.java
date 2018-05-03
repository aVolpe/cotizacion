package py.com.volpe.cotizacion;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.gatherer.Gatherer;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Arturo Volpe
 * @since 5/2/18
 */
@Service
@AllArgsConstructor
@Log4j2
public class GathererManager {

	private final List<Gatherer> gathererList;

	public String init(String code) {
		return doAction("INIT", code, Gatherer::addOrUpdatePlace);
	}

	public String doQuery(String code) {
		return doAction("GATHER", code, Gatherer::doQuery);
	}

	private String doAction(String operationName, String code, Consumer<Gatherer> action) {

		log.info("{} Initializing", operationName);
		Consumer<Gatherer> safeAction = g -> {
			try {
				log.info("{} running on {}", operationName, g.getCode());
				action.accept(g);
				log.info("{} end ok on {}", operationName, g.getCode());
			} catch (Exception e) {
				log.warn("{} The gatherer {} throws an exception", operationName, g.getCode(), e);
			}
		};

		if (code != null) {
			gathererList.stream().filter(g -> g.getCode().equals(code)).forEach(safeAction);
		} else {
			gathererList.parallelStream().forEach(safeAction);
		}

		log.info("{} Ending", operationName);
		return code;
	}
}

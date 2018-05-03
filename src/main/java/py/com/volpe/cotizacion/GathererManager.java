package py.com.volpe.cotizacion;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.gatherer.Gatherer;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Arturo Volpe
 * @since 5/2/18
 */
@Service
@Transactional
@AllArgsConstructor
public class GathererManager {

	private final List<Gatherer> gathererList;

	public String init(String code) {
		return doAction(code, Gatherer::addOrUpdatePlace);
	}

	public String doQuery(String code) {
		return doAction(code, Gatherer::doQuery);
	}

	private String doAction(String code, Consumer<Gatherer> action) {

		if (code != null) {
			gathererList.stream().filter(g -> g.getCode().equals(code)).forEach(action);
			return code;
		} else {
			gathererList.forEach(action);
			return "all";
		}
	}
}

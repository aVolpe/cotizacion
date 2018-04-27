package py.com.volpe.cotizacion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.com.volpe.cotizacion.gatherer.Gatherer;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@RestController
@RequiredArgsConstructor
public class PlaceController {

	private final List<Gatherer> gathererList;

	@GetMapping(value = "/api/places/init", produces = MediaType.APPLICATION_JSON_VALUE)
	public String init(@RequestParam(value = "code", required = false) String code) {
		System.out.println("PlaceController.init");
		System.out.println("code = [" + code + "]");
		return doAction(code, Gatherer::addOrUpdatePlace);
	}

	@GetMapping(value = "/api/places/doQuery", produces = MediaType.APPLICATION_JSON_VALUE)
	public String doQuery(@RequestParam(value = "code", required = false) String code) {
		System.out.println("PlaceController.doQuery");
		System.out.println("code = [" + code + "]");
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

	@GetMapping("/hellow")
	public String test() {

		return "hellow world";
	}
}

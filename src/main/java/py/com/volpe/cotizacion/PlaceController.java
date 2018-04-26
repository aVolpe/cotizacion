package py.com.volpe.cotizacion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.volpe.cotizacion.gatherer.CambiosChaco;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@RestController
@RequiredArgsConstructor
public class PlaceController {

	private final CambiosChaco cambiosChaco;

	@GetMapping(value = "/api/places/init", produces = MediaType.APPLICATION_JSON_VALUE)
	public String init() {

		cambiosChaco.addOrUpdatePlace();
		return "ok";
	}

	@GetMapping(value = "/api/places/doQuery", produces = MediaType.APPLICATION_JSON_VALUE)
	public String doQuery() {

		cambiosChaco.doQuery();
		return "ok";
	}

	@GetMapping("/hellow")
	public String test() {

		return "hellow world";
	}
}

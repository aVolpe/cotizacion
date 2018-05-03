package py.com.volpe.cotizacion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.com.volpe.cotizacion.GathererManager;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@RestController
@RequiredArgsConstructor
@Profile("develop")
public class PlaceController {

	private final GathererManager manager;

	@GetMapping(value = "/api/places/init", produces = MediaType.APPLICATION_JSON_VALUE)
	public String init(@RequestParam(value = "code", required = false) String code) {
		return manager.init(code);
	}

	@GetMapping(value = "/api/places/doQuery", produces = MediaType.APPLICATION_JSON_VALUE)
	public String doQuery(@RequestParam(value = "code", required = false) String code) {
		return manager.doQuery(code);
	}

}

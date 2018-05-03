package py.com.volpe.cotizacion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import py.com.volpe.cotizacion.repository.QueryResponseDetailRepository;
import py.com.volpe.cotizacion.repository.QueryResponseDetailRepository.ByIsoCodeResult;

import java.util.List;

/**
 * @author Arturo Volpe
 * @since 4/27/18
 */
@RestController
@RequiredArgsConstructor
public class ExchangeController {

	private final QueryResponseDetailRepository detailRepository;


	@GetMapping(value = "/api/exchange/{iso}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ByIsoCodeResult> byIso(@PathVariable(value = "iso") String code) {
		return detailRepository.getMaxByPlaceInISO(code);
	}

	@GetMapping(value = "/api/exchange/", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getAvailableCurrencies() {
		return detailRepository.getAvailableISO();
	}

}

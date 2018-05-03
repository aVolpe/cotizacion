package py.com.volpe.cotizacion.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Arturo Volpe
 * @since 5/3/18
 */
@RestController
@RequiredArgsConstructor
public class LicenseController {

	private final ResourceLoader loader;

	@GetMapping(value = "/api/licenses", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getLicenses() throws IOException {
		Resource data = loader.getResource("classpath:licenses.json");

		if (!data.exists())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(IOUtils.toByteArray(data.getInputStream()), HttpStatus.OK);
	}
}

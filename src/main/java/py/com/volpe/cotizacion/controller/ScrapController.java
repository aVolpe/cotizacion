package py.com.volpe.cotizacion.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.com.volpe.cotizacion.GathererManager;

import java.util.Collections;
import java.util.Set;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@RestController
public class ScrapController {

    private final GathererManager manager;
    private final String apiKey;

    public ScrapController(GathererManager manager,
                           @Value("${API_KEY:123789123789}") String apiKey) {
        this.manager = manager;
        this.apiKey = apiKey;
    }


    @GetMapping(value = "/api/scrap/places/init", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> init(@RequestParam(value = "code", required = false) String code,
                            @RequestParam(value = "api_key") String passedKey) {
        if (this.apiKey.equals(passedKey))
            return manager.init(code);
        return Collections.emptySet();
    }

    @GetMapping(value = "/api/scrap/places/doQuery", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> doQuery(@RequestParam(value = "code", required = false) String code,
                               @RequestParam(value = "api_key") String passedKey
    ) {
        if (this.apiKey.equals(passedKey))
            return manager.doQuery(code);
        return Collections.emptySet();
    }

}

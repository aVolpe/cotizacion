package py.com.volpe.cotizacion.controller;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Renders the client's index.html with the data of the first screen already loaded
 * (<code>window.ssr</code>, read by the exchange store), so the page doesn't need to wait for the API.
 * <p>
 * It also serves the client routes (history mode), so a direct link to /map or /licenses opens the app.
 *
 * @author Arturo Volpe
 * @since 10/25/20
 */
@Controller
@AllArgsConstructor
@Log4j2
public class SSR {

    private final ObjectMapper mapper;
    private final ExchangeController exchanges;

    /**
     * Same paths as client/src/router.ts.
     */
    @GetMapping(value = {"/", "/about", "/map", "/licenses", "/swagger", "/no-back"})
    public String doQuery(
            Model model,
            @RequestParam(name = "moneda", required = false, defaultValue = "USD") String currency
    ) {

        // The template inlines it as a JS string literal (escaped by Thymeleaf), the client parses it
        model.addAttribute("ssr", buildState(currency));
        return "index";
    }

    private String buildState(String currency) {

        Map<String, Object> state = new HashMap<>();
        try {
            state.put("exchanges", Collections.singletonMap(currency, loaded(exchanges.byIso(currency))));
            state.put("currencies", loaded(exchanges.getAvailableCurrencies()));
            state.put("current", buildCurrent(currency));
            return mapper.writeValueAsString(state);
        } catch (JacksonException e) {
            log.warn("Can't pre-render 'index' with currency {}", currency, e);
            return null;
        }
    }

    private Map<String, Object> loaded(Object toWrap) {

        Map<String, Object> toRet = new HashMap<>();
        toRet.put("loading", false);
        toRet.put("loaded", true);
        toRet.put("data", toWrap);
        return toRet;
    }

    private Map<String, Object> buildCurrent(String currency) {
        Map<String, Object> toRet = new HashMap<>();
        toRet.put("currency", currency);
        toRet.put("amount", 1);
        return toRet;
    }
}

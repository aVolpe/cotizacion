package py.com.volpe.cotizacion.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * @author Arturo Volpe
 * @since 10/25/20
 */
@Controller
@AllArgsConstructor
@Log4j2
public class SSR {

    public static final String UNDEFINED = "undefined";
    private final ObjectMapper mapper;
    private final ExchangeController exchanges;

    @GetMapping(value = "/")
    public String doQuery(
            Model model,
            @RequestParam(name = "moneda", required = false, defaultValue = "USD") String currency
    ) {

        try {
            model.addAttribute("exchanges", mapper.writeValueAsString(Collections.singletonMap(
                    currency, loaded(exchanges.byIso(currency)))
            ));
            model.addAttribute("branches", UNDEFINED);
            model.addAttribute("currencies", mapper.writeValueAsString(loaded(exchanges.getAvailableCurrencies())));
            model.addAttribute("current", mapper.writeValueAsString(buildCurrent(currency)));
        } catch (JsonProcessingException e) {
            log.warn("Can't pre-render 'index' with currency {}", currency, e);
            model.addAttribute("exchanges", UNDEFINED);
            model.addAttribute("branches", UNDEFINED);
            model.addAttribute("currencies", UNDEFINED);
            model.addAttribute("current", UNDEFINED);
        }
        return "index";
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

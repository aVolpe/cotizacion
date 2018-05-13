package py.com.volpe.cotizacion.controller;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import py.com.volpe.cotizacion.repository.QueryResponseDetailRepository;
import py.com.volpe.cotizacion.repository.QueryResponseDetailRepository.ByIsoCodeResult;

import java.util.Date;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;

/**
 * @author Arturo Volpe
 * @since 4/27/18
 */
@RestController
@RequiredArgsConstructor
public class ExchangeController {

    private final QueryResponseDetailRepository detailRepository;


    @GetMapping(value = "/api/exchange/{iso}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultData byIso(@PathVariable(value = "iso") String code) {

        List<ByIsoCodeResult> data = detailRepository.getMaxByPlaceInISO(code);

        LongSummaryStatistics lsm = data.stream().map(ByIsoCodeResult::getQueryDate).collect(Collectors.summarizingLong(Date::getTime));

        return new ResultData(
                lsm.getMin() == Long.MAX_VALUE ? null : new Date(lsm.getMin()),
                lsm.getMax() == Long.MIN_VALUE ? null : new Date(lsm.getMax()),
                lsm.getCount(),
                data);
    }

    @GetMapping(value = "/api/exchange/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getAvailableCurrencies() {
        return detailRepository.getAvailableISO();
    }


    @Value
    public static class ResultData {
        private Date firstQueryResult;
        private Date lastQueryResult;
        private long count;
        private List<ByIsoCodeResult> data;
    }
}

package py.com.volpe.cotizacion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.volpe.cotizacion.domain.Execution;
import py.com.volpe.cotizacion.domain.ExchangeRate;
import py.com.volpe.cotizacion.domain.QueryResponse;
import py.com.volpe.cotizacion.domain.QueryResponseDetail;
import py.com.volpe.cotizacion.repository.ExchangeRateRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stores the result of a query: a rate that didn't change only moves the {@code lastSeen} of its
 * {@link ExchangeRate}, a new price adds a new row.
 */
@Service
public class ExchangeRateRecorder {

    private final ExchangeRateRepository repository;

    public ExchangeRateRecorder(ExchangeRateRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<ExchangeRate> record(QueryResponse response, Execution execution) {

        Map<String, ExchangeRate> latest = findLatest(response).stream()
                .collect(Collectors.toMap(ExchangeRate::getIsoCode, Function.identity()));

        List<ExchangeRate> toRet = new ArrayList<>();
        for (QueryResponseDetail detail : response.getDetails()) {
            ExchangeRate current = latest.get(detail.getIsoCode());
            if (current != null && current.hasTheSamePrices(detail)) {
                current.seenAgain(response.getDate(), execution);
            } else {
                if (current != null) {
                    // flushed before the insert, only one row per place/branch/currency can be the latest
                    current.setLatest(false);
                    repository.saveAndFlush(current);
                }
                current = new ExchangeRate(response, detail, execution);
                latest.put(detail.getIsoCode(), current);
            }
            toRet.add(repository.save(current));
        }
        return toRet;
    }

    private List<ExchangeRate> findLatest(QueryResponse response) {
        return response.getBranch() == null
                ? repository.findLatest(response.getPlace())
                : repository.findLatest(response.getPlace(), response.getBranch());
    }
}

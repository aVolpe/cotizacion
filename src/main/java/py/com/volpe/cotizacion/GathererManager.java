package py.com.volpe.cotizacion;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.gatherer.Gatherer;
import py.com.volpe.cotizacion.repository.PlaceRepository;
import py.com.volpe.cotizacion.repository.QueryResponseRepository;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Arturo Volpe
 * @since 5/2/18
 */
@Service
@AllArgsConstructor
@Log4j2
public class GathererManager {

    private final List<Gatherer> gathererList;
    private final PlaceRepository placeRepository;
    private final QueryResponseRepository queryResponseRepository;

    public List<String> init(String code) {
        return doAction("INIT", code, this::getPlace);
    }

    @CacheEvict(cacheNames = {"byIso", "isoList"}, allEntries = true)
    public List<String> doQuery(String code) {
        return doAction("GATHER", code, this::doQuery);
    }

    private List<String> doAction(String operationName, String code, Consumer<Gatherer> action) {

        log.info("{} Initializing", operationName);
        Function<Gatherer, String> safeAction = g -> {
            try {
                log.info("{} running on {}", operationName, g.getCode());
                action.accept(g);
                log.info("{} end ok on {}", operationName, g.getCode());
                return g.getCode();
            } catch (Exception e) {
                log.warn("{} The gatherer {} throws an exception", operationName, g.getCode(), e);
                return null;
            }
        };

        Stream<String> toRet;
        if (code != null) {
            toRet = gathererList.stream().filter(g -> g.getCode().equals(code)).map(safeAction);
        } else {
            toRet = gathererList.parallelStream().map(safeAction);
        }

        log.info("{} end", operationName);
        return toRet.filter(Objects::nonNull).collect(Collectors.toList());

    }

    private void doQuery(Gatherer g) {
        Place p = getPlace(g);
        g.doQuery(p, p.getBranches())
                .forEach(queryResponseRepository::save);
    }

    private Place getPlace(Gatherer g) {
        return placeRepository.findByCode(g.getCode()).orElseGet(() -> placeRepository.save(g.build()));
    }
}

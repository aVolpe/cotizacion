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
 * This is the main entry point of this application
 * <p>
 * This class is in charge of creating (and in a future to update) the place data,
 * and later query the exchanges in every place.
 *
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

    /**
     * This is in charge of initializing the database (place and branches).
     *
     * @param code the code of the place, null for all places
     * @return the list of places initialized
     */
    public List<String> init(String code) {
        return doAction("INIT", code, this::getPlace);
    }

    /**
     * This is in charge of performing a exchange query over a place
     *
     * @param code the code of the place (in case we want to only query one place)
     * @return the list of places with new data
     */
    @CacheEvict(cacheNames = {"byIso", "isoList"}, allEntries = true)
    public List<String> doQuery(String code) {
        return doAction("GATHER", code, this::doQuery);
    }

    /**
     * Do an action on a Gatherer, the action is safe.
     * <p>
     * Safe in this context means that if the action throws an exception, the this method will
     * handle and log the error, allowing subsequent invocations to work.
     *
     * @param name   the name of this operation, for logging purposes
     * @param code   the code over which gatherer the action mus be done, null to do the operation over all.
     * @param action the action to do
     * @return a list with the codes of every gatherer where the action was performed successfully
     */
    private List<String> doAction(String name, String code, Consumer<Gatherer> action) {

        log.info("{} Initializing", name);

        Function<Gatherer, String> safeAction = gatherer -> {
            try {
                log.info("{} running on {}", name, gatherer.getCode());
                action.accept(gatherer);
                log.info("{} end ok on {}", name, gatherer.getCode());
                return gatherer.getCode();
            } catch (Exception e) {
                log.warn("{} The gatherer {} throws an exception", name, gatherer.getCode(), e);
                return null;
            }
        };

        Stream<String> toRet;
        if (code != null) {
            toRet = gathererList.stream().filter(g -> g.getCode().equals(code)).map(safeAction);
        } else {
            toRet = gathererList.parallelStream().map(safeAction);
        }

        log.info("{} end", name);
        return toRet.filter(Objects::nonNull).collect(Collectors.toList());

    }

    /**
     * Make a query in a gatherer
     * <p>
     * This methods is in charge of provides the correct parameters.
     *
     * @param gatherer the gatherer.
     */
    private void doQuery(Gatherer gatherer) {
        Place p = getPlace(gatherer);
        gatherer.doQuery(p, p.getBranches())
                .forEach(queryResponseRepository::save);
    }

    /**
     * Check if the place exists and if it doesn't, it create the place.
     *
     * @param gatherer the gatherer
     * @return the persisted place
     */
    private Place getPlace(Gatherer gatherer) {
        return placeRepository
                .findByCode(gatherer.getCode())
                .orElseGet(() -> placeRepository.save(gatherer.build()));
    }
}

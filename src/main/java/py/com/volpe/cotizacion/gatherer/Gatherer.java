package py.com.volpe.cotizacion.gatherer;

import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.QueryResponse;

import java.util.List;
import java.util.Optional;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
public interface Gatherer {

	List<QueryResponse> doQuery();

	Optional<Place> get();

	Place addOrUpdatePlace();

	String getCode();
}

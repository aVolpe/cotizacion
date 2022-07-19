package py.com.volpe.cotizacion.gatherer;

import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.domain.QueryResponse;

import java.util.List;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
public interface Gatherer {

    /**
     * Query the data from this place.
     * <p>
     * This method can trow exceptions.
     *
     * @return the result of the query
     */
    List<QueryResponse> doQuery(Place place, List<PlaceBranch> branches);

    /**
     * Unique code to represent this place
     *
     * @return the not null place code
     */
    String getCode();
}

package py.com.volpe.cotizacion.gatherer;

import py.com.volpe.cotizacion.domain.Place;
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
     * This method should store the data in the DB
     * <p>
     * This method can trow exceptions.
     *
     * @return the persisted list of data
     */
    List<QueryResponse> doQuery();

    /**
     * Get the current place (search by CODE)
     *
     * @return the optional place
     */
    Place get();


    /**
     * Unique code to represent this place
     *
     * @return the not null place code
     */
    String getCode();
}

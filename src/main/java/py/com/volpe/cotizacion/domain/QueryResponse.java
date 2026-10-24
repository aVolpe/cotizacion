package py.com.volpe.cotizacion.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The result of querying a place (or one of its branches), as returned by a gatherer.
 * <p>
 * It is not persisted as is: {@link py.com.volpe.cotizacion.ExchangeRateRecorder} turns its details into
 * {@link ExchangeRate} rows, which only grow when a price changes.
 *
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Data
public class QueryResponse {

    private Date date;

    private Place place;

    private PlaceBranch branch;

    private List<QueryResponseDetail> details;

    public QueryResponse() {
        this.date = new Date();
        this.details = new ArrayList<>();
    }

    public QueryResponse(Place place) {
        this();
        this.place = place;
    }

    public QueryResponse(PlaceBranch pb) {
        this(pb.getPlace());
        this.branch = pb;
    }

    public QueryResponse addDetail(QueryResponseDetail detail) {
        this.getDetails().add(detail);
        return this;
    }

    public void setDetails(List<QueryResponseDetail> details) {
        this.details = details == null ? new ArrayList<>() : details;
    }
}

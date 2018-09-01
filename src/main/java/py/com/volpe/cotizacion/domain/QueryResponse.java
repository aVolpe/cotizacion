package py.com.volpe.cotizacion.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {

    @Id
    @GeneratedValue
    private long id;
    private Date date;

    @ManyToOne
    private Place place;

    @ManyToOne
    private PlaceBranch branch;

    private String fullData;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "queryResponse")
    private List<QueryResponseDetail> details;

    @ManyToOne
    private Execution execution;

    public QueryResponse(PlaceBranch pb) {
        this.place = pb.getPlace();
        this.date = new Date();
        this.branch = pb;
        this.details = new ArrayList<>();
    }

    public void setDetails(List<QueryResponseDetail> details) {
        if (details == null) this.details = new ArrayList<>();
        else this.details = details;
        this.details.forEach(d -> d.setQueryResponse(this));
    }

    public void addDetail(QueryResponseDetail detail) {
        this.getDetails().add(detail);
        detail.setQueryResponse(this);
    }
}

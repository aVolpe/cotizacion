package py.com.volpe.cotizacion.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

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
		detail.setQueryResponse(this);
		return this;
	}
}

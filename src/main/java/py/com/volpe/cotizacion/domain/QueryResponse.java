package py.com.volpe.cotizacion.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Data
@Entity
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

}

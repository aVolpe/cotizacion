package py.com.volpe.cotizacion.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Data
@Entity
public class Place {

	@Id
	@GeneratedValue
	private long id;

	private String code;
	private String name;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "place")
	private List<PlaceBranch> branches;
}

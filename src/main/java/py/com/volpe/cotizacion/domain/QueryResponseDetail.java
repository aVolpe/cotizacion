package py.com.volpe.cotizacion.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Data
@Entity
public class QueryResponseDetail {

	@Id
	@GeneratedValue
	private long id;
	private long purchasePrice;
	private long salePrice;
	private long purchaseTrend;
	private long saleTrend;
	private String isoCode;

	@ManyToOne
	private QueryResponse queryResponse;
}

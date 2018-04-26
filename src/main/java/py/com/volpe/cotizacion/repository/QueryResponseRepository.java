package py.com.volpe.cotizacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.com.volpe.cotizacion.domain.QueryResponse;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
public interface QueryResponseRepository extends JpaRepository<QueryResponse, Long> {

}

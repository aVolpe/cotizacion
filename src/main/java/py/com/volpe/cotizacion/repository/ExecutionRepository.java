package py.com.volpe.cotizacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.com.volpe.cotizacion.domain.Execution;

/**
 * @author Arturo Volpe
 * @since 7/22/18
 */
public interface ExecutionRepository extends JpaRepository<Execution, Long> {

}


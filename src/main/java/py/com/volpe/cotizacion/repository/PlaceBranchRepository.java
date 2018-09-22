package py.com.volpe.cotizacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.com.volpe.cotizacion.domain.PlaceBranch;

import java.util.List;

/**
 * @author Arturo Volpe
 * @since 9/21/18
 */
public interface PlaceBranchRepository extends JpaRepository<PlaceBranch, Long> {

    List<PlaceBranch> getByPlaceCode(String placeCode);
}



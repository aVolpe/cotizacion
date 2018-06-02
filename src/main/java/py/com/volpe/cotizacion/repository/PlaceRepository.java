package py.com.volpe.cotizacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import py.com.volpe.cotizacion.domain.Place;

import java.util.Optional;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByCode(String code);
}

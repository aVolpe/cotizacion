package py.com.volpe.cotizacion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import py.com.volpe.cotizacion.domain.PlaceBranch;
import py.com.volpe.cotizacion.repository.PlaceBranchRepository;

import java.util.List;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@RestController
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceBranchRepository branchRepository;

    @GetMapping(value = "/api/places/{code}/branches", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PlaceBranch> init(@PathVariable(value = "code") String code) {
        return branchRepository.getByPlaceCode(code);
    }


}

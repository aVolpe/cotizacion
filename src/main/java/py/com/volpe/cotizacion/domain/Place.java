package py.com.volpe.cotizacion.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
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
public class Place {

    @Id
    @GeneratedValue
    private long id;

    private String code;
    private String name;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "place", fetch = FetchType.EAGER)
    @Singular
    private List<PlaceBranch> branches;
}

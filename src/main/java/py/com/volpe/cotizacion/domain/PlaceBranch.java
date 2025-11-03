package py.com.volpe.cotizacion.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

/**
 * @author Arturo Volpe
 * @since 4/26/18
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "place")
public class PlaceBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    private long id;

    private Double latitude;
    private Double longitude;

    private String phoneNumber;
    private String email;
    private String image;
    private String name;

    private String schedule;
    private String remoteCode;

    @JsonIgnore
    @ManyToOne
    private Place place;

    public PlaceBranch(long id, String remoteCode) {
        this.id = id;
        this.remoteCode = remoteCode;
    }

}

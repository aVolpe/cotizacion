package py.com.volpe.cotizacion.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Arturo Volpe
 * @since 7/22/18
 */
@Data
@Entity
@AllArgsConstructor
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    private long id;

    private Date date;

    @OneToMany(mappedBy = "execution")
    private List<QueryResponse> responses;

    public Execution() {
        date = new Date();
    }
}

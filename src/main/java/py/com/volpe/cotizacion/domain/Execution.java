package py.com.volpe.cotizacion.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    @GeneratedValue
    private long id;

    private Date date;

    @OneToMany(mappedBy = "execution")
    private List<QueryResponse> responses;

    public Execution() {
        date = new Date();
    }
}

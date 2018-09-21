package py.com.volpe.cotizacion.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

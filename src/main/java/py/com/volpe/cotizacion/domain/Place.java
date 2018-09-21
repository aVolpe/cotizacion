package py.com.volpe.cotizacion.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
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

    public enum Type {
        BUREAU,
        BANK
    }

    @Id
    @GeneratedValue
    private long id;

    private String code;
    private String name;

    @Enumerated(EnumType.STRING)
    private Type type;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "place", fetch = FetchType.EAGER)
    @Singular
    private List<PlaceBranch> branches;

    @PostLoad
    private void init() {
        if (this.type == null) {
            this.type = Type.BUREAU;
        }
    }

    public Place(String name, String code) {
        this.name = name;
        this.code = code;
        this.branches = new ArrayList<>();
    }

    public void setBranches(List<PlaceBranch> branches) {
        if (branches == null) this.branches = new ArrayList<>();
        else this.branches = branches;

        this.branches.forEach(b -> b.setPlace(this));
    }

    public void addBranch(PlaceBranch newBranch) {
        if (this.branches == null) this.branches = new ArrayList<>();
        newBranch.setPlace(this);
        this.branches.add(newBranch);
    }
}

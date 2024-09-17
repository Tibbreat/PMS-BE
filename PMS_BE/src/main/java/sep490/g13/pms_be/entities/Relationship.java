package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Relationship extends Auditable<String> {

    @ManyToOne
    @JoinColumn(name = "children_id")
    private Children childrenId;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parentId;

    private String relationship;

    private Boolean isRepresentative;
}

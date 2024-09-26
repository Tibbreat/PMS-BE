package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Relationship extends Auditable<String> {

    @ManyToOne
    @JoinColumn(name = "children_id", nullable = false)
    @JsonIgnore
    private Children childrenId;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonIgnore
    private User parentId;

    private String relationship;

    private Boolean isRepresentative;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relationship that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(childrenId, that.childrenId) && Objects.equals(parentId, that.parentId) && Objects.equals(relationship, that.relationship) && Objects.equals(isRepresentative, that.isRepresentative);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), childrenId, parentId, relationship, isRepresentative);
    }
}

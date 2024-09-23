package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Children extends Auditable<String> {
    private String childName;
    private Integer childAge;
    private LocalDate childBirthDate;
    private String childAddress;

    private Boolean isRegisteredForTransport = Boolean.FALSE;
    private Boolean isRegisteredForBoarding = Boolean.FALSE;

    private String imageUrl;

    private String cloudinaryImageId;

    @OneToMany(mappedBy = "children")
    private Set<ChildrenFee> childrenFees;

    @OneToMany(mappedBy = "childrenId")
    private Set<Relationship> relationships;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonBackReference
    private Classes schoolClass;
}

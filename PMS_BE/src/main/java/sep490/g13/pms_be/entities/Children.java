package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "children", cascade = CascadeType.ALL, orphanRemoval = true)

    private Set<ChildrenFee> childrenFees;


    @OneToMany(mappedBy = "childrenId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Relationship> relationships;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonBackReference
    private Classes schoolClass;
}

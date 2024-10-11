package sep490.g13.pms_be.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
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

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classes schoolClass;

    @OneToMany(mappedBy = "childrenId", cascade = CascadeType.ALL)
    private Set<Relationship> relationships = new HashSet<>();
}

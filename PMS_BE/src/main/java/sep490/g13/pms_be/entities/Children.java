package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
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

    private Boolean isRegisteredForTransport;
    private Boolean isRegisteredForBoarding;

    @OneToMany
    private Set<Relationship> relationships;

}

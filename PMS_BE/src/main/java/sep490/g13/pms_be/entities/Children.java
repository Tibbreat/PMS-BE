package sep490.g13.pms_be.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
}

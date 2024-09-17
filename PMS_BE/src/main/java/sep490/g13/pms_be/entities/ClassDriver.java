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
public class ClassDriver extends Auditable<String> {

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes classesId;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driverId;
}

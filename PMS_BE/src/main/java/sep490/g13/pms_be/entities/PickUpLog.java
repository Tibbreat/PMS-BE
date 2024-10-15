package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickUpLog extends Auditable<String>  {

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Children children;

    @CreationTimestamp
    private LocalDate pickupDate;

    @CreationTimestamp
    private LocalTime pickupTime;

    @ManyToOne
    @JoinColumn(name = "responsible_person_id")
    private User user;
}

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceLog extends Auditable<String>  {
    @ManyToOne
    private Children children;

    @CreationTimestamp
    private LocalDate attendanceDate;

    @CreationTimestamp
    private LocalTime attendanceTime;

    @ManyToOne
    @JoinColumn(name = "responsible_person_id")
    private User user;
}

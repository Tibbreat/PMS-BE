package sep490.g13.pms_be.entities;

import jakarta.persistence.*;
import lombok.*;

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

    private LocalDate attendanceDate;

    private LocalTime checkinTime;

    private LocalTime checkoutTime;

    @Lob
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes classes;
}

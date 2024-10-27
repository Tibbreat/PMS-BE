package sep490.g13.pms_be.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyMenu extends Auditable<String> {

    private LocalDate date;

    private String ageRange;

    private String breakfast;
    private String lunch;
    private String afternoon;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String note;

}

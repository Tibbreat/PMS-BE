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

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class clazz;

    private LocalDate menuDate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String breakfast;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String lunch;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String snacks;

    private String note;
}

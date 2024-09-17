package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class DailyMealInfo extends Auditable<String> {
    @ManyToOne
    private Children child;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class clazz;

    private LocalDate mealDate;

    private Boolean isOptedOut;

    private String reasonForOptOut;
}

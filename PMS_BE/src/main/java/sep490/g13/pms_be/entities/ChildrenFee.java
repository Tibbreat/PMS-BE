package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
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
public class ChildrenFee extends Auditable<String> {
    @ManyToOne
    private Children children;

    @ManyToOne
    private Fee fee;

    private Boolean isPayed;

    private LocalDate dueDate;

    private String amount;
}

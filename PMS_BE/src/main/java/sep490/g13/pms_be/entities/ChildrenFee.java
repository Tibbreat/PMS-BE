package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class ChildrenFee extends Auditable<String> {
    @ManyToOne
    @JoinColumn(name = "children_id")
    @JsonIgnore
    private Children children;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "fee_id")

    private Fee fee;

    private Boolean isPayed;

    private LocalDate dueDate;

    private String amount;
}

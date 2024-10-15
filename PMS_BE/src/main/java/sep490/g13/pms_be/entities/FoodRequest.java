package sep490.g13.pms_be.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
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
public class FoodRequest extends Auditable<String> {
    private LocalDate requestDate;

    private String item;

    private String quantity;

    private String unit;

    private String deliveryAddress;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String note;
}

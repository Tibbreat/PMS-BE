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
public class FoodRequestItem extends Auditable<String> {
    private String foodName;
    private String quantity;
    private String note;

    @ManyToOne
    @JoinColumn(name = "food_request_id")
    private FoodRequest foodRequest;
}

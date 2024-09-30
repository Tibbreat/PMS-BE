package sep490.g13.pms_be.model.request.food;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodRequestDTO {
    private LocalDate requestDate;
    private String item;
    private String quantity;
    private String unit;
    private String deliveryAddress;
    private String note;
    private String createdById;
}

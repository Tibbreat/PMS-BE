package sep490.g13.pms_be.model.request.food;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFoodRequest {

    private LocalDate requestDate;

    private String item;

    private String quantity;

    private String unit;

    private String deliveryAddress;

    private String note;
    @NotNull(message = "Không được để trống")
    private String lastModifiedById;
}

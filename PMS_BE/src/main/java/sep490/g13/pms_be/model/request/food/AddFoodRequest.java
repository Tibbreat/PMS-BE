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
public class AddFoodRequest {
    @NotNull(message = "Không được để trống")
    private LocalDate requestDate;
    @NotNull(message = "Không được để trống")
    private String item;
    @NotNull(message = "Không được để trống")
    private String quantity;
    @NotNull(message = "Không được để trống")
    private String unit;
    @NotNull(message = "Không được để trống")
    private String deliveryAddress;

    private String note;
    @NotNull(message = "Không được để trống")
    private String createdById;
}

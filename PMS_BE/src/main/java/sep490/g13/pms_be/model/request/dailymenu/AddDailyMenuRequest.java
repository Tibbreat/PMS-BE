package sep490.g13.pms_be.model.request.dailymenu;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.g13.pms_be.entities.Classes;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddDailyMenuRequest {
    @NotNull(message = "Không được để trống")
    private String classId;
    @NotNull(message = "Không được để trống")
    private LocalDate menuDate;
    @NotNull(message = "Không được để trống")
    private String breakfast;
    @NotNull(message = "Không được để trống")
    private String lunch;
    @NotNull(message = "Không được để trống")
    private String snacks;

    private String note;
    @NotNull(message = "Không được để trống")
    private String createdById;
}

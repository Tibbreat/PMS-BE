package sep490.g13.pms_be.model.request.dailymenu;

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
public class UpdateDailyMenuRequest {


    private String breakfast;

    private String lunch;

    private String snacks;

    private String note;
    @NotNull(message = "Không được để trống")
    private String lastModifiedById;
}

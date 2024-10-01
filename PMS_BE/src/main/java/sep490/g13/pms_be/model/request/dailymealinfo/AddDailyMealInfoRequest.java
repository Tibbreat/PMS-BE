package sep490.g13.pms_be.model.request.dailymealinfo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddDailyMealInfoRequest {

    @NotNull(message = "Không được để trống")
    private String childId;

    @NotNull(message = "Không được để trống")
    private String dailyMenuId;

    private Boolean isOptedOut;

    private String reasonForOptOut;

    @NotNull(message = "Không được để trống")
    private String createdById;
}

package sep490.g13.pms_be.model.request.dailymealinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDailyMealRequest {

    private Boolean isOptedOut;

    private String reasonForOptOut;
    private String lastModifiedByID;
}

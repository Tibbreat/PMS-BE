package sep490.g13.pms_be.model.response.dailymenu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyMenuResponse {
    private String classId;
    private LocalDate menuDate;
    private String breakfast;
    private String lunch;
    private String snacks;
    private String note;
}
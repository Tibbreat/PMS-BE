package sep490.g13.pms_be.model.response.dailymealinfo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyMealInfoResponse {
    private String childId; // ID của trẻ
    private String dailyMenuId; // ID của thực đơn
    private Boolean isOptedOut; // Có chọn không
    private String reasonForOptOut; // Lý do không chọn

}

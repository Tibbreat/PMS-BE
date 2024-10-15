package sep490.g13.pms_be.model.response.classes;

import lombok.*;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassListResponse {
    private String id;
    private String className;
    private String ageRange;
    private Date closingDay;
    private Date openingDay;
    private String managerId;
    private String managerName;
    private boolean status;
}

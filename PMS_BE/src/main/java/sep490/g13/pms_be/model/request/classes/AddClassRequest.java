package sep490.g13.pms_be.model.request.classes;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddClassRequest {
    @NotNull(message = "Class Name không được để trống")
    private String className;
    @NotNull(message = "Tuổi không được để trống")
    private String ageRange;
    @NotNull(message = "Opening Day không được để trống")
    private Date openingDay;
    @NotNull(message = "Closing Day không được để trống")
    private Date closingDay;

    @NotNull(message = "Danh sách giáo viên không được để trống")
    private List<String> teacherId;
    @NotNull(message = "Quản lý lớp không được để trống")
    private String managerId;
    @NotNull(message= "Created By Không được để trống")
    private String createdBy;


}

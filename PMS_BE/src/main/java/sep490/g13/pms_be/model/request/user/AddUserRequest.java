package sep490.g13.pms_be.model.request.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import sep490.g13.pms_be.utils.enums.RoleEnums;
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class AddUserRequest {

    @NotEmpty(message = "Họ và tên không được để trống")
    private String fullName;

    @NotNull(message = "Role không được để trống")
    private RoleEnums role;
}

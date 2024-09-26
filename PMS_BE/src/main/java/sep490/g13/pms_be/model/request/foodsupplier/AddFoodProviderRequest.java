package sep490.g13.pms_be.model.request.foodsupplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddFoodProviderRequest {
    @NotBlank(message = "Tên người đại diện không được bỏ trống")
    private String representativeName;

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String providerName;

    @NotBlank(message = "Số điện thoại nhà cung cấp k")
    private String providerPhone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải đúng định dạng")
    private String providerEmail;

    @NotBlank(message = "Địa chỉ nhà cung cấp không được để trống")
    private String providerAddress;

    @NotBlank(message = "Số đăng ký kinh doanh không được để trống")
    private String providerRegisterNumber;

    @NotBlank(message = "Số giấy phép kinh doanh không được để trống")
    private String providerLicenseNumber;

    @NotNull(message= "Người tạo không được để trống")
    private String createdBy;
}

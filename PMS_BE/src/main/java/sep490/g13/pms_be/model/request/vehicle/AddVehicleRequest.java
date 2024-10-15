package sep490.g13.pms_be.model.request.vehicle;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddVehicleRequest {
    @NotNull(message = "Biển số xe không được để trống")
    private String licensePlate;
    @NotNull(message = "Màu không được để trống")
    private String color;
    @NotNull(message="Kiểu dáng không được để trống")
    private String model;
    @NotNull(message="Nhãn hiệu không được để trống")
    private String brand;
    @NotNull(message="trung tâm cung cấp xe không được để trống")
    private String transportServiceProviderId;
    @NotNull(message = "Người tạo không được để trống")
    private String createdById;
}

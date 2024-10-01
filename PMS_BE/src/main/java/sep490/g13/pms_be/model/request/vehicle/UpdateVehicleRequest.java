package sep490.g13.pms_be.model.request.vehicle;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVehicleRequest {
    private String licensePlate;
    private String color;
    private String model;
    private String brand;
    private String transportProviderId; // ID of the transport provider
    private String lastModifiedById; // ID of the user modifying the vehicle
}
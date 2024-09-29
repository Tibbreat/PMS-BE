package sep490.g13.pms_be.model.response.vehicle;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class VehicleDetailResponse {
    private String licensePlate;
    private String color;
    private String model;
    private String brand;
    private String transportProviderId; // ID of the transport provider
}
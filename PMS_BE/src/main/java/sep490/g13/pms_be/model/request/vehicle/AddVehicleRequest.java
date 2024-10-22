package sep490.g13.pms_be.model.request.vehicle;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddVehicleRequest {
    private String vehicleName;
    private String manufacturer;
    private int numberOfSeats;
    private String color;
    private String licensePlate;
    private String driverName;
    private String driverPhone;

    private String pickUpLocation;

    private String createdBy;
    private String providerId;
}

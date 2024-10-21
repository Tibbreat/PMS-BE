package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vehicle extends Auditable<String> {
    private String vehicleName;          // Tên xe
    private String manufacturer;         // Hãng xe
    private int numberOfSeats;           // Số chỗ
    private String color;                // Màu xe
    private String licensePlate;         // Biển số xe
    private String driverName;           // Tên tài xế
    private String driverPhone;          // Số điện thoại tài xế

    private Boolean isActive;
    private String pickUpLocation;
    private String timeStart;

    @ManyToOne
    @JoinColumn(name = "transportId", nullable = false)
    @JsonBackReference
    private TransportServiceProvider transport;
}
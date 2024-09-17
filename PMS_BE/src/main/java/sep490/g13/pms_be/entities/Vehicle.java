package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vehicle extends Auditable<String> {
    private String licensePlate;
    private String color;
    private String model;
    private String brand;

    @ManyToOne
    private TransportServiceProvider transport;
}

package sep490.g13.pms_be.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
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

    @OneToOne
    private User driver;
}

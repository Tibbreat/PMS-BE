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
    private String licensePlate;
    private String color;
    private String model;
    private String brand;

    @ManyToOne
    @JoinColumn(name = "transportId", nullable = false)
    @JsonBackReference
    private TransportServiceProvider transport;
}

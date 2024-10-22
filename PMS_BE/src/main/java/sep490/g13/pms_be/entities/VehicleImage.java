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
public class VehicleImage extends Auditable<String> {

    private String imageUrl;
    private String imageType;
    @ManyToOne
    private Vehicle vehicle;
}

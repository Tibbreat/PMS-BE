package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver extends Auditable<String> {
    private String fullName;
    private String phoneNumber;
    private String idCardNumber;
    private String address;
    private String imageLink;
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "transprt_provider_id")
    private TransportServiceProvider transportServiceProvider;
}

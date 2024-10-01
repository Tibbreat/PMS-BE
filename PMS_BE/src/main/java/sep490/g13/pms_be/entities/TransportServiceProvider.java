package sep490.g13.pms_be.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransportServiceProvider extends Auditable<String> {
    private String representativeName;
    private String providerName;
    private String providerPhone;
    private String providerEmail;
    private String providerAddress;
    private String providerRegisterNumber;
    private String providerLicenseNumber;
    private String contractFile;

    @Column(nullable = false)
    private Boolean isActive = false;

    @OneToMany(mappedBy = "transport")
    private Set<Vehicle> vehicles;
}

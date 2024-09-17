package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodServiceProvider extends Auditable<String> {
    private String representativeName;
    private String providerName;
    private String providerPhone;
    private String providerEmail;
    private String providerAddress;
    private String providerRegisterNumber;
    private String providerLicenseNumber;
    private String contractFile;
    private Boolean isActive;
}

package sep490.g13.pms_be.model.request.foodsupplier;


import jakarta.persistence.Entity;
import lombok.*;
import sep490.g13.pms_be.entities.Auditable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodProviderAddNewRequest {


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

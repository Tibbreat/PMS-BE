package sep490.g13.pms_be.model.response.foodsupplier;

import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetFoodProviderResponse {
    private String providerName;

    private String providerPhone;

    private String providerEmail;

    private String providerAddress;

    private String providerRegisterNumber;

    private String providerLicenseNumber;

    private String representativeName;

    private Boolean isActive;

    private String contractFile;

    private String createdBy;

    private Date createdDate;

    private String  lastModifiedBy;

    private Date lastModifiedDate;
}

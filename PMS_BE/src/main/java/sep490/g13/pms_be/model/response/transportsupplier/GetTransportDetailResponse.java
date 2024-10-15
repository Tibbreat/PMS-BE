package sep490.g13.pms_be.model.response.transportsupplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetTransportDetailResponse {
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

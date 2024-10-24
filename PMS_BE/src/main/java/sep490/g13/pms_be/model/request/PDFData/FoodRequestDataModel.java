package sep490.g13.pms_be.model.request.PDFData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FoodRequestDataModel {
    //page 1
    private String signedCity;
    private String signedDay;
    private String signedMonth;
    private String signedYear;

    private String contractNumber;
    private String schoolName;

    private String providerName;
    private String providerAddress;
    private String providerTaxCode;
    private String providerPhone;
    private String representativeName;
    private String representativePosition;

    private String schoolAddress;
    private String phoneContact;
    private String principalName;

    private String neededDate;

    //page 2
    private String bankName;
    private String bankAccountNumber;
    private String beneficiaryName;
    private String signedDate;

    private String SignChar;


}

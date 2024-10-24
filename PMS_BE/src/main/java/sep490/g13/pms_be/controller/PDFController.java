package sep490.g13.pms_be.controller;


import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.g13.pms_be.entities.School;
import sep490.g13.pms_be.model.request.PDFData.FoodRequestDataModel;
import sep490.g13.pms_be.repository.SchoolRepo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/pms/pdf")
public class PDFController {

    @Autowired
    private SchoolRepo schoolRepo;

    @PostMapping("/food-request-pdf")
    public ResponseEntity<byte[]> fillPdf(@RequestBody FoodRequestDataModel data) {
        try {
            // Đọc template PDF từ classpath
            InputStream pdfTemplate = getClass().getResourceAsStream("/tmp/FRC-TMP.pdf");
            if (pdfTemplate == null) {
                throw new RuntimeException("File PDF template không tồn tại!");
            }

            PdfReader pdfReader = new PdfReader(pdfTemplate);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Tạo PdfStamper để chỉnh sửa PDF
            PdfStamper stamper = new PdfStamper(pdfReader, outputStream);
            AcroFields form = stamper.getAcroFields();

            // Điền dữ liệu vào các trường form
            form.setField("signedCity", data.getSignedCity());
            form.setField("signedDay", data.getSignedDay());
            form.setField("signedMonth", data.getSignedMonth());
            form.setField("signedYear", data.getSignedYear());
            form.setField("contractNumber", data.getContractNumber());
            form.setField("schoolName", data.getSchoolName());
            form.setField("providerName", data.getProviderName());
            form.setField("providerAddress", data.getProviderAddress());
            form.setField("providerTaxCode", data.getProviderTaxCode());
            form.setField("providerPhone", data.getProviderPhone());
            form.setField("representativeName", data.getRepresentativeName());
            form.setField("representativePosition", data.getRepresentativePosition());
            form.setField("schoolAddress", data.getSchoolAddress());
            form.setField("phoneContact", data.getPhoneContact());
            form.setField("principalName", data.getPrincipalName());
            form.setField("neededDate", data.getNeededDate());
            form.setField("bankName", data.getBankName());
            form.setField("bankAccountNumber", data.getBankAccountNumber());
            form.setField("beneficiaryName", data.getBeneficiaryName());
            form.setField("signedDate", data.getSignedDate());
            form.setField("SignChar", data.getSignChar());


            stamper.setFormFlattening(true);
            stamper.close();

            // Trả về file PDF đã điền dữ liệu
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "filledForm.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}

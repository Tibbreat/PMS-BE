package sep490.g13.pms_be.controller;


import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.g13.pms_be.model.request.PDFData.FoodRequestDataModel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/pms/pdf")
public class PDFController {

    @PostMapping("/fill-pdf")
    public ResponseEntity<byte[]> fillPdf(@RequestBody FoodRequestDataModel data) {
        try {
            // Đọc template PDF từ classpath
            InputStream pdfTemplate = getClass().getResourceAsStream("/pdf-template/templatecheck.pdf");
            if (pdfTemplate == null) {
                throw new RuntimeException("File PDF template không tồn tại!");
            }

            PdfReader pdfReader = new PdfReader(pdfTemplate);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Tạo PdfStamper để chỉnh sửa PDF
            PdfStamper stamper = new PdfStamper(pdfReader, outputStream);
            AcroFields form = stamper.getAcroFields();

            // Điền dữ liệu vào các trường form
            form.setField("fullName", data.getFullName());

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
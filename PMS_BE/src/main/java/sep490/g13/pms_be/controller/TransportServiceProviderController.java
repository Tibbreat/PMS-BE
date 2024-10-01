package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.TransportServiceProvider;
import sep490.g13.pms_be.model.request.transportsupplier.AddTransportProviderRequest;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.TransportServiceProviderService;

@RestController
@RequestMapping("/api/transportServiceProvider")
public class TransportServiceProviderController {

    @Autowired
    private TransportServiceProviderService transportServiceProviderService;

    @PostMapping("/add")
    public ResponseEntity<ResponseModel<?>> addNewProvider(
            @RequestPart("tpa") @Valid AddTransportProviderRequest tpa,
            @RequestParam("contractFile") MultipartFile contractFile,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: " + "\n");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(";");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseModel.<TransportServiceProvider>builder()
                            .message(errorMessage.toString())
                            .data(null)
                            .build());
        }

        try {
            // Gọi service để thêm nhà cung cấp
            TransportServiceProvider savedTSP = transportServiceProviderService.addTransportProvider(tpa, contractFile);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseModel.<TransportServiceProvider>builder()
                            .message("Nhà cung cấp thức ăn được thêm thành công")
                            .data(savedTSP)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseModel.<TransportServiceProvider>builder()
                            .message("Nhà cung cấp thức ăn thêm bị lỗi: " + e.getMessage())
                            .data(null)
                            .build());
        }
    }
}
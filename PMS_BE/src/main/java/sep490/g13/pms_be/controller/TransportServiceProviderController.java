package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.TransportServiceProvider;
import sep490.g13.pms_be.model.request.transportsupplier.AddTransportProviderRequest;
import sep490.g13.pms_be.model.request.transportsupplier.UpdateTransportRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.model.response.transportsupplier.GetTransportDetailResponse;
import sep490.g13.pms_be.service.entity.TransportServiceProviderService;
import sep490.g13.pms_be.utils.ValidationUtils;

import java.util.List;

@RestController
@RequestMapping("/pms/transportServiceProvider")
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

    @GetMapping
    public ResponseEntity<PagedResponseModel<TransportServiceProvider>> getTransportProvider(
            @RequestParam int page,
            @RequestParam(required = false) Boolean status) {
        int size = 10;
        Page<TransportServiceProvider> results = transportServiceProviderService.getTransportProvider(status, page - 1, size);
        List<TransportServiceProvider> tsp = results.getContent();
        String msg = "";
        if (tsp.isEmpty()) {
            msg = "Không có nhà cung cấp dịch vụ vận chuyển nào";
        } else {
            msg = "Có " + tsp.size() + " nhà cung cấp dịch vụ vận chuyển";
        }
        PagedResponseModel<TransportServiceProvider> pagedResponse = PagedResponseModel.<TransportServiceProvider>builder()
                .page(page)
                .total(results.getTotalElements())
                .size(size)
                .msg(msg)
                .listData(tsp)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @PutMapping("/change-information/{transportProviderId}")
    public ResponseEntity<ResponseModel<?>> updateTransportProvider(
            @RequestBody @Valid UpdateTransportRequest updateTransportRequest,
            @PathVariable String transportProviderId,
            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            String errorMessage = ValidationUtils.getValidationErrors(bindingResult);

            return ResponseEntity.badRequest()
                    .body(ResponseModel.<String>builder()
                            .message("Cập nhật nhà cung cấp dịch vụ vận chuyển lỗi: " + errorMessage)
                            .data(errorMessage)
                            .build());
        }
        transportServiceProviderService.updateTransportProvider(updateTransportRequest, transportProviderId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseModel.<String>builder()
                        .message("Nhà cung cấp dịch vụ vận chuyển được cập nhật thành công")
                        .data(null)
                        .build());
    }

    @PutMapping("/change-status")
    public ResponseEntity<ResponseModel<?>> changeStatus(
            @RequestParam String transportProviderId,
            @RequestParam Boolean status) {
        TransportServiceProvider updateStatus = transportServiceProviderService.updateStatus(transportProviderId, status);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseModel.<TransportServiceProvider>builder()
                        .message("Trạng thái nhà cung cấp đã được cập nhật thành công")
                        .data(updateStatus)
                        .build());
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<ResponseModel<?>> getFoodProviderDetail(@PathVariable String providerId) {
        GetTransportDetailResponse getTransportDetailResponse = transportServiceProviderService.getTransportProviderDetailById(providerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseModel.<GetTransportDetailResponse>builder()
                        .message("Thành công")
                        .data(getTransportDetailResponse)
                        .build());
    }
}
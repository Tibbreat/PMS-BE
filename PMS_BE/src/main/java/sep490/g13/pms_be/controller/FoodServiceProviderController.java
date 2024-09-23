package sep490.g13.pms_be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.FoodServiceProvider;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.FoodServiceProviderService;
import sep490.g13.pms_be.service.utils.DriveService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/foodServiceProvider")
public class FoodServiceProviderController {

    @Autowired
    private FoodServiceProviderService foodServiceProviderService;

    @Autowired
    private DriveService driveService;

    @PostMapping("/add")
    public ResponseEntity<ResponseModel<FoodServiceProvider>> addProvider(
            @RequestParam("provider") String providerJson,
            @RequestParam("contract") MultipartFile contract) {

        try {
            // Chuyển JSON sang đối tượng FoodServiceProvider
            ObjectMapper objectMapper = new ObjectMapper();
            FoodServiceProvider foodServiceProvider = objectMapper.readValue(providerJson, FoodServiceProvider.class);

            // Xử lý file hợp đồng
            File tempFile = File.createTempFile("Contract - " + foodServiceProvider.getProviderName() + " - ", ".pdf");
            contract.transferTo(tempFile);

            // Upload hợp đồng lên Google Drive
            foodServiceProvider.setContractFile(driveService.upload(tempFile));  // Lưu đường dẫn vào trường contractFile


            // Xóa file tạm sau khi upload
            tempFile.delete();

            // Lưu nhà cung cấp mới
            FoodServiceProvider savedProvider = foodServiceProviderService.addProvider(foodServiceProvider);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseModel.<FoodServiceProvider>builder()
                            .message("Provider added successfully with contract uploaded")
                            .data(savedProvider)
                            .build());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseModel.<FoodServiceProvider>builder()
                            .message("Failed to add provider or upload contract: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<PagedResponseModel<FoodServiceProvider>> getProvider(
            @RequestParam int page,
            @RequestParam(required = false) String status) {
        int size = 10;
        Page<FoodServiceProvider> results = foodServiceProviderService.getProvider(status, page - 1, size);
        List<FoodServiceProvider> fsp = results.getContent();
        String msg = "";
        if (fsp.isEmpty()) {
            msg = "Không có nhà cung cấp nào";
        } else {
            msg = "Có " + fsp.size() + " nhà cung cấp";
        }
        PagedResponseModel<FoodServiceProvider> pagedResponse = PagedResponseModel.<FoodServiceProvider>builder()
                .page(page)
                .total(results.getTotalElements())
                .size(size)
                .msg(msg)
                .listData(fsp)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

}


package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.FoodServiceProvider;
import sep490.g13.pms_be.model.request.foodsupplier.FoodProviderAddNewRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.FoodServiceProviderService;
import java.util.List;

@RestController
@RequestMapping("/api/foodServiceProvider")
public class FoodServiceProviderController {

    @Autowired
    private FoodServiceProviderService foodServiceProviderService;


    @PostMapping("/add")
    public ResponseEntity<ResponseModel<?>> addNewProvider(
            @ModelAttribute @Valid FoodProviderAddNewRequest fpa,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: " + "\n");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(";");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseModel.<FoodServiceProvider>builder()
                            .message(errorMessage.toString())
                            .data(null)
                            .build());
        }
        try {

            FoodServiceProvider savedFSP = foodServiceProviderService.addFoodProvider(fpa);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseModel.<FoodServiceProvider>builder()
                            .message("Food Provider added successfully")
                            .data(savedFSP)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseModel.<FoodServiceProvider>builder()
                            .message("Food Provider added failed: " + e.getMessage())
                            .data(null)
                            .build());
        }

    }


    @GetMapping
    public ResponseEntity<PagedResponseModel<FoodServiceProvider>> getProvider(
            @RequestParam int page,
            @RequestParam(required = false) Boolean status) {
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


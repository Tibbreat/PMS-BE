package sep490.g13.pms_be.controller;



import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.model.request.food.AddFoodRequest;
import sep490.g13.pms_be.model.request.food.UpdateFoodRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.FoodRequestService;
import sep490.g13.pms_be.entities.FoodRequest;
import sep490.g13.pms_be.utils.ValidationUtils;

import java.util.List;

@RestController
@RequestMapping("/pms/food")
public class FoodController {

    @Autowired
    private FoodRequestService foodRequestService;

    @PostMapping("/add")
    public ResponseEntity<ResponseModel<FoodRequest>> addFoodRequest(@RequestBody AddFoodRequest addFoodRequest) {
        FoodRequest foodRequest = foodRequestService.addFoodRequest(addFoodRequest);
        ResponseModel<FoodRequest> request = ResponseModel.<FoodRequest>builder()
                .message("Food request added successfully")
                .data(foodRequest)
                .build();
        return ResponseEntity.ok(request);
    }
    @GetMapping()
    public ResponseEntity<PagedResponseModel<FoodRequest>> getFoodRequests(
            @RequestParam int page,
            @RequestParam(required = false) String item) {

        int size = 10; // Số lượng mỗi trang
        Page<FoodRequest> result = foodRequestService.getFood(item, page - 1, size);

        List<FoodRequest> foodRequestList = result.getContent();

        // Tạo response với phân trang
        PagedResponseModel<FoodRequest> pagedResponse = PagedResponseModel.<FoodRequest>builder()
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .listData(foodRequestList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }
    @PutMapping("/change-food-description/{foodRequestId}")
    public ResponseEntity<ResponseModel<?>> updateFoodRequestDescription(
            @RequestBody @Valid UpdateFoodRequest updateFoodRequest,
            BindingResult bindingResult,
            @PathVariable String foodRequestId) {

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ResponseModel.<String>builder()
                            .message("Cập nhật không thành công")
                            .data(ValidationUtils.getValidationErrors(bindingResult))
                            .build());
        }

        // Gọi service để thực hiện cập nhật
        foodRequestService.updateFoodRequest(foodRequestId, updateFoodRequest);

        // Trả về response thành công
        return ResponseEntity.ok(ResponseModel.<String>builder()
                .message("Cập nhật thông tin yêu cầu thực phẩm thành công")
                .data(null)
                .build());
    }
}

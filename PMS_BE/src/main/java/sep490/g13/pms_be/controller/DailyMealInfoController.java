package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.DailyMealInfo;
import sep490.g13.pms_be.model.request.dailymealinfo.AddDailyMealInfoRequest;
import sep490.g13.pms_be.model.request.dailymealinfo.UpdateDailyMealRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.model.response.dailymealinfo.DailyMealInfoResponse;
import sep490.g13.pms_be.service.entity.DailyMealInfoService;
import sep490.g13.pms_be.utils.ValidationUtils;

import java.util.List;

@RestController
@RequestMapping("/pms/daily-meal-info")
public class DailyMealInfoController {
    @Autowired
    private DailyMealInfoService dailyMealInfoService;
    @PostMapping("/add")
    public ResponseEntity<ResponseModel<?>> addDailyMealInfo(
            @RequestBody @Valid AddDailyMealInfoRequest addDailyMealInfoRequest,
            BindingResult bindingResult) {

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ResponseModel.<String>builder()
                            .message("Thêm thông tin bữa ăn không thành công")
                            .data(ValidationUtils.getValidationErrors(bindingResult))
                            .build());
        }

        // Thêm thông tin bữa ăn mới
        DailyMealInfo dailyMealInfo = dailyMealInfoService.addDailyMealInfo(addDailyMealInfoRequest);

        return ResponseEntity.ok(
                ResponseModel.builder()
                        .message("Thêm thông tin bữa ăn thành công")
                        .data(addDailyMealInfoRequest) // Bạn có thể điều chỉnh dữ liệu trả về tùy ý
                        .build());
    }
    @GetMapping()
    public ResponseEntity<PagedResponseModel<DailyMealInfoResponse>> getDailyMealInfo(
            @RequestParam(required = false) String childId,
            @RequestParam(required = false) String dailyMenuId,
            @RequestParam int page) {

        int size = 10; // Số lượng thực đơn mỗi trang
        Page<DailyMealInfoResponse> result = dailyMealInfoService.getDailyMealInfo(childId, dailyMenuId, page - 1, size);

        List<DailyMealInfoResponse> dailyMealInfoList = result.getContent();

        // Tạo response có phân trang
        PagedResponseModel<DailyMealInfoResponse> pagedResponse = PagedResponseModel.<DailyMealInfoResponse>builder()
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .listData(dailyMealInfoList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }
    @PutMapping("/change-daily-meal-description/{mealInfoId}")
    public ResponseEntity<ResponseModel<?>> changeDailyMealDescription(
            @PathVariable String mealInfoId,
            @RequestBody @Valid UpdateDailyMealRequest updateRequest,
            BindingResult bindingResult) {

        // Kiểm tra lỗi đầu vào
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ResponseModel.<String>builder()
                            .message("Cập nhật không thành công")
                            .data(ValidationUtils.getValidationErrors(bindingResult))
                            .build());
        }

        // Gọi service để cập nhật DailyMealInfo
        dailyMealInfoService.changeDailyMealDescription(mealInfoId, updateRequest);

        // Trả về phản hồi thành công
        return ResponseEntity.ok(ResponseModel.<String>builder()
                .message("Cập nhật thông tin bữa ăn hàng ngày thành công")
                .data(null)
                .build());
    }


}

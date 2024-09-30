package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.DailyMenu;
import sep490.g13.pms_be.model.request.dailymenu.AddDailyMenuRequest;
import sep490.g13.pms_be.model.request.dailymenu.UpdateDailyMenuRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.model.response.dailymenu.DailyMenuResponse;
import sep490.g13.pms_be.service.entity.DailyMenuService;
import sep490.g13.pms_be.utils.ValidationUtils;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pms/daily-menu")
public class DailyMenuController {
    @Autowired
    private DailyMenuService dailyMenuService;

    @PostMapping("/add")
    public ResponseEntity<ResponseModel<?>> addDailyMenu(
            @RequestBody @Valid AddDailyMenuRequest addDailyMenuRequest,
            BindingResult bindingResult) {

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ResponseModel.<String>builder()
                            .message("Thêm thực đơn không thành công")
                            .data(ValidationUtils.getValidationErrors(bindingResult))
                            .build());
        }

        // Trả về response thành công
        DailyMenu dailyMenu = dailyMenuService.addDailyMenu(addDailyMenuRequest);
        return ResponseEntity.ok(
                ResponseModel.builder()
                        .message("Thêm lớp học thành công")
                        .data(addDailyMenuRequest)
                        .build());
    }

    @GetMapping()
    public ResponseEntity<PagedResponseModel<DailyMenuResponse>> getDailyMenus(
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) LocalDate menuDate,
            @RequestParam int page
            ) {
        int size = 10;
        Page<DailyMenuResponse> result = dailyMenuService.getDailyMenus(classId, menuDate, page - 1, size);

        List<DailyMenuResponse> dailyMenuList = result.getContent();

        // Tạo response có phân trang
        PagedResponseModel<DailyMenuResponse> pagedResponse = PagedResponseModel.<DailyMenuResponse>builder()
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .listData(dailyMenuList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }
    @PutMapping("/change-menu-description/{menuId}")
    public ResponseEntity<ResponseModel<?>> changeDailyMenuDescription(
            @PathVariable String menuId,
            @RequestBody @Valid UpdateDailyMenuRequest updateRequest,
            BindingResult bindingResult) {

        // Kiểm tra lỗi đầu vào
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ResponseModel.<String>builder()
                            .message("Cập nhật không thành công")
                            .data(ValidationUtils.getValidationErrors(bindingResult))
                            .build());
        }

        // Gọi service để cập nhật daily menu
        dailyMenuService.changeDailyMenuDescription(menuId, updateRequest);

        // Trả về phản hồi thành công
        return ResponseEntity.ok(ResponseModel.<String>builder()
                .message("Cập nhật thực đơn thành công")
                .data(null)
                .build());
    }
}

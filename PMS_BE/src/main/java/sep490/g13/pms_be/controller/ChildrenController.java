package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.*;
import sep490.g13.pms_be.model.request.children.AddChildrenRequest;
import sep490.g13.pms_be.model.request.children.UpdateChildrenRequest;
import sep490.g13.pms_be.model.request.user.AddUserRequest;
import sep490.g13.pms_be.model.response.CloudinaryResponse;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.model.response.children.ChildrenDetailResponse;
import sep490.g13.pms_be.model.response.children.ChildrenListResponse;
import sep490.g13.pms_be.service.utils.CloudinaryService;
import sep490.g13.pms_be.service.entity.ChildrenService;
import sep490.g13.pms_be.service.entity.ClassService;
import sep490.g13.pms_be.service.entity.UserService;
import sep490.g13.pms_be.utils.ValidationUtils;

import java.util.List;

@RestController
@RequestMapping("/pms/children")
public class ChildrenController {
    @Autowired
    private ChildrenService childrenService;


    @GetMapping
    public ResponseEntity<PagedResponseModel<ChildrenListResponse>> getChildren(
            @RequestParam(value = "classId", required = false) String classId,
            @RequestParam(value = "childName", required = false) String childName,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        int size = 10;
        Page<ChildrenListResponse> results = childrenService.findChildrenByFilter(classId, childName, page - 1, size);
        List<ChildrenListResponse> childrenList = results.getContent();

        String msg = childrenList.isEmpty() ? "Không có dữ liệu" : "Tìm thấy " + results.getTotalElements();
        PagedResponseModel<ChildrenListResponse> pagedResponse = PagedResponseModel.<ChildrenListResponse>builder()
                .page(page)
                .size(size)
                .msg(msg)
                .total(results.getTotalElements())
                .listData(childrenList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @PostMapping("/new-children")
    public ResponseEntity<ResponseModel<?>> addChildren(
            @RequestPart("children") @Valid AddChildrenRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseModel.<String>builder()
                            .message("Add children failed")
                            .data(ValidationUtils.getValidationErrors(bindingResult))
                            .build());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseModel.<Children>builder()
                        .message("Add children successfully")
                        .data(childrenService.addChildren(request, image))
                        .build());
    }

    @GetMapping("/{childrenId}")
    public ResponseEntity<ResponseModel<ChildrenDetailResponse>> getChildrenDetail(@PathVariable String childrenId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseModel.<ChildrenDetailResponse>builder()
                        .message("Get children detail successfully")
                        .data(childrenService.getChildrenDetail(childrenId))
                        .build());
    }
}

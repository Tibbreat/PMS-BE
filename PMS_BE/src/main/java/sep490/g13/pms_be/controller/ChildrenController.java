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
import sep490.g13.pms_be.model.response.CloudinaryResponse;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.model.response.children.ChildrenDetailResponse;
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
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassService classService;



    @PostMapping("/add")
    public ResponseEntity<ResponseModel<?>> addChild(
            @RequestPart("image") MultipartFile image,
            @Valid @RequestPart("request") AddChildrenRequest request, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            String validationErrors = ValidationUtils.getValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(
                    ResponseModel.builder()
                            .message("Thông tin trẻ không hợp lệ")
                            .data(validationErrors)
                            .build()
            );
        }
        try {
            // Tạo đối tượng Children
            Children child = new Children();
            child.setChildName(request.getChildName());
            child.setChildAge(request.getChildAge());
            child.setChildBirthDate(request.getChildBirthDate());
            child.setChildAddress(request.getChildAddress());
            child.setCreatedBy(request.getCreatedById());

            // Thiết lập class cho đứa trẻ
            Classes schoolClass = classService.getClassById(request.getClassId());
            child.setSchoolClass(schoolClass);

            // Lưu đứa trẻ và các mối quan hệ (relationships)
            Children savedChildren = childrenService.addChildren(child, request.getRelationships());

            // Kiểm tra và tải lên hình ảnh nếu có
            if (image != null && !image.isEmpty()) {
                CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(image, savedChildren.getId().toString());
                // Cập nhật URL và ID hình ảnh trong đối tượng Children
                savedChildren.setImageUrl(cloudinaryResponse.getUrl());
                savedChildren.setCloudinaryImageId(cloudinaryResponse.getPublicId());
                // Lưu lại đối tượng Children đã cập nhật
                childrenService.updateChildren(savedChildren);
            }

            return ResponseEntity.ok(
                    ResponseModel.builder()
                            .message("Thêm học sinh thành công")
                            .data(savedChildren)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseModel.builder()
                            .message("Đã xảy ra lỗi khi thêm học sinh: " + e.getMessage())
                            .build()
            );
        }
    }


    @GetMapping()
    public ResponseEntity<PagedResponseModel<Children>> getChildren(
            @RequestParam int page,
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String classId) {

        int size = 10;
        Page<Children> result = childrenService.getChildrenByFilters(fullname, classId, page - 1, size);


        result.getContent().forEach(children -> {
            Hibernate.initialize(children.getSchoolClass());
        });

        List<Children> childrenList = result.getContent();


        PagedResponseModel<Children> pagedResponse = PagedResponseModel.<Children>builder()
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .listData(childrenList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @GetMapping("/children-detail/{childId}")
    public ResponseEntity<ResponseModel<?>> getClassDetail(@PathVariable String childId) {
        ChildrenDetailResponse childrenDetailResponse = childrenService.getChildrenDetailById(childId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseModel.<ChildrenDetailResponse>builder()
                        .message("Lấy dữ liệu học sinh có id: " + childId)
                        .data(childrenDetailResponse)
                        .build());
    }

    @PutMapping("/update-transport/{childId}")
    public ResponseEntity<String> updateTransportRegistration(
            @PathVariable String childId,
            @RequestParam Boolean isRegisteredForTransport) {
        childrenService.updateTransportRegistration(childId, isRegisteredForTransport);
        return ResponseEntity.ok("Transport registration updated successfully");
    }

    @PutMapping("update-boarding/{childId}")
    public ResponseEntity<String> updateBoardingRegistration(
            @PathVariable String childId,
            @RequestParam Boolean isRegisteredForBoarding) {
        childrenService.updateBoardingRegistration(childId, isRegisteredForBoarding);
        return ResponseEntity.ok("Boarding registration updated successfully");
    }
    @PutMapping("/change-information/{childId}")
    public ResponseEntity<ResponseModel<?>> updateChildren(
            @PathVariable String childId,
            @RequestPart("updateRequest") UpdateChildrenRequest updateChildrenRequest,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            Children updateChildren = childrenService.updateChildrenInformation(childId, updateChildrenRequest);
            if (image != null && !image.isEmpty()) {
                CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(image, childId);

                updateChildren.setImageUrl(cloudinaryResponse.getUrl());
                updateChildren.setCloudinaryImageId(cloudinaryResponse.getPublicId());
                // Lưu lại đối tượng Children đã cập nhật
                childrenService.updateChildren(updateChildren);
            }
            return ResponseEntity.ok(new ResponseModel<>("Update successful", updateChildrenRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseModel<>(e.getMessage(), null));
        }
    }


    @GetMapping("/children-by-class/{classId}")
    public ResponseEntity<PagedResponseModel<Children>> getChildrenByClass(
            @PathVariable String classId,
            @RequestParam int page){
        int size = 10;
        Page<Children> results = childrenService.getChildrenByClass(classId, size, page - 1);
        List<Children> children = results.getContent();
        String msg = children.isEmpty() ? "Không có dữ liệu" : "Tìm thấy " + results.getTotalElements() + " dữ liệu";
        PagedResponseModel<Children> pagedResponse = PagedResponseModel.<Children>builder()
                .page(page)
                .size(size)
                .msg(msg)
                .total(results.getTotalElements())
                .listData(children)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }
}

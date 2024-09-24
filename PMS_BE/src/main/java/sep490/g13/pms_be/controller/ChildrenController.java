package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.*;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.RelationshipRequest;
import sep490.g13.pms_be.model.request.children.AddChildrenRequest;
import sep490.g13.pms_be.model.request.classes.AddClassRequest;
import sep490.g13.pms_be.model.response.CloudinaryResponse;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.service.CloudinaryService;
import sep490.g13.pms_be.service.entity.ChildrenService;
import sep490.g13.pms_be.service.entity.ClassService;
import sep490.g13.pms_be.service.entity.UserService;
import sep490.g13.pms_be.utils.ValidationUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/children")
public class ChildrenController {
    @Autowired
    private ChildrenService childrenService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassService classService;



    @PostMapping("/addChild")
    public ResponseEntity<ResponseModel<?>> addChild(
            @RequestPart("image") MultipartFile image,  // Sử dụng @RequestParam cho tệp ảnh
            @RequestPart("request") AddChildrenRequest request) {  // Sử dụng @RequestPart cho dữ liệu JSON
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

}

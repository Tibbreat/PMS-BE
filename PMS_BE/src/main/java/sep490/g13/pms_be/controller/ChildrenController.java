package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.g13.pms_be.entities.*;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.RelationshipRequest;
import sep490.g13.pms_be.model.request.children.AddChildrenRequest;
import sep490.g13.pms_be.model.request.classes.AddClassRequest;
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
    public ResponseEntity<ResponseModel<?>> addChild(@RequestBody AddChildrenRequest request) {
        Children child = new Children();
        child.setChildName(request.getChildName());
        child.setChildAge(request.getChildAge());
        child.setChildBirthDate(request.getChildBirthDate());
        child.setChildAddress(request.getChildAddress());

        // Thiết lập lớp
        Classes schoolClass = classService.getClassById(request.getClassId());

        child.setSchoolClass(schoolClass);

        // Tạo danh sách relationships
        if (request.getRelationships() != null) {
            for (RelationshipRequest relationshipRequest : request.getRelationships()) {
                Relationship relationship = new Relationship();
                User parent = userService.getUserById(relationshipRequest.getParentId());


                relationship.setParentId(parent);  // Ánh xạ parent từ request
                relationship.setChildrenId(child);  // Gán quan hệ với Children
                relationship.setRelationship(relationshipRequest.getRelationship());  // Ánh xạ relationship type
                relationship.setIsRepresentative(relationshipRequest.getIsRepresentative());  // Ánh xạ isRepresentative

                child.getRelationships().add(relationship);  // Thêm vào tập relationships của child
            }
        }

        // Lưu Children và Relationships vào cơ sở dữ liệu
        Children savedChildren = childrenService.addChildren(child);

        try {

            return ResponseEntity.ok(
                    ResponseModel.builder()
                            .message("Thêm học sinh thành công")
                            .data(savedChildren)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseModel.builder()
                            .message("Lỗi khi thêm học sinh: " + e.getMessage())
                            .build()
            );
        }
    }

}

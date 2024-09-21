package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.classes.AddClassRequest;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.ChildrenService;
import sep490.g13.pms_be.service.entity.ClassService;
import sep490.g13.pms_be.service.entity.TeacherService;
import sep490.g13.pms_be.service.entity.UserService;
import sep490.g13.pms_be.utils.ValidationUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/classes")
public class ClassController {
    @Autowired
    private ClassService classService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChildrenService childrenService;
    @Autowired
    private TeacherService teacherService;


    @PostMapping("/add")
    public ResponseEntity<ResponseModel<?>> addNewJob(
            @RequestBody @Valid AddClassRequest classRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String validationErrors = ValidationUtils.getValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(
                    ResponseModel.builder()
                            .message("Thông tin lớp học không hợp lệ")
                            .data(validationErrors)
                            .build()
            );
        }
        Set<Children> assignedChildren = childrenService.findChildrenWithClasses(classRequest.getChildrenId());
        if (!assignedChildren.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseModel.builder()
                            .message("Một số trẻ đã được gán vào lớp khác: " + assignedChildren)
                            .data(assignedChildren)
                            .build()
            );
        }
        Classes newClass = new Classes();
        BeanUtils.copyProperties(classRequest, newClass);
        Set<Children> childrens = new HashSet<>(childrenService.findAllById(classRequest.getChildrenId()));
        newClass.setChildren(childrens);
        Set<ClassTeacher> teachers = new HashSet<>(teacherService.findAllById(classRequest.getTeacherId()));
        newClass.setTeachers(teachers);
        User manager = userService.getUserById(classRequest.getManagerId());
        if (manager.getRole().equals(RoleEnums.Class_Manager)) {
            newClass.setManager(manager);
        } else {
            throw new PermissionNotAcceptException("Người này không có vai trò là Quản lý lớp (Class_Manager)");
        }





        Classes savedClass = classService.addClass(newClass);
        return ResponseEntity.ok(
                ResponseModel.builder()
                        .message("Thêm lớp học thành công")
                        .data(savedClass)
                        .build()
        );
    }


}

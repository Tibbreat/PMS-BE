package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.classes.AddClassRequest;
import sep490.g13.pms_be.model.request.classes.UpdateClassRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.model.response.classes.ClassDetailResponse;
import sep490.g13.pms_be.service.entity.ChildrenService;
import sep490.g13.pms_be.service.entity.ClassService;
import sep490.g13.pms_be.service.entity.TeacherService;
import sep490.g13.pms_be.service.entity.UserService;
import sep490.g13.pms_be.utils.ValidationUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.HashSet;
import java.util.List;
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
    public ResponseEntity<ResponseModel<?>> addNewClass(
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

        Classes newClass = new Classes();
        BeanUtils.copyProperties(classRequest, newClass);

        Set<ClassTeacher> teachers = new HashSet<>();
        for (String teacherId : classRequest.getTeacherId()) {
            User teacher = userService.getUserById(teacherId);
            if (teacher == null) {
                return ResponseEntity.badRequest().body(
                        ResponseModel.builder()
                                .message("Giáo viên không tồn tại: " + teacherId)
                                .build()
                );
            }
            ClassTeacher classTeacher = new ClassTeacher();
            classTeacher.setSchoolClasses(newClass);
            classTeacher.setTeacherId(teacher);
            teachers.add(classTeacher);
        }
        newClass.setTeachers(teachers);

        User manager = userService.getUserById(classRequest.getManagerId());
        if (manager == null) {
            return ResponseEntity.badRequest().body(
                    ResponseModel.builder()
                            .message("Quản lý không tồn tại: " + classRequest.getManagerId())
                            .build()
            );
        }

        if (!manager.getRole().equals(RoleEnums.Class_Manager)) {
            throw new PermissionNotAcceptException("Người này không có vai trò là Quản lý lớp (Class_Manager)");
        }

        newClass.setManager(manager);

        try {
            Classes savedClass = classService.addClass(newClass);
            return ResponseEntity.ok(
                    ResponseModel.builder()
                            .message("Thêm lớp học thành công")
                            .data(savedClass)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseModel.builder()
                            .message("Lỗi khi thêm lớp học: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping()
    public ResponseEntity<PagedResponseModel<Classes>> getClasses(
            @RequestParam int page,
            @RequestParam(required = false) Integer schoolYear,
            @RequestParam(required = false) String ageRange,
            @RequestParam(required = false) Long managerId) {

        int size = 10; // Số lượng lớp học mỗi trang
        Page<Classes> result = classService.getClasses(schoolYear, ageRange, managerId, page - 1, size);

        // Initialize các liên kết cho các đối tượng lớp học
        result.getContent().forEach(classes -> {
            Hibernate.initialize(classes.getManager());
            Hibernate.initialize(classes.getChildren());
            Hibernate.initialize(classes.getTeachers());
        });

        List<Classes> classesList = result.getContent();

        // Tạo response với phân trang
        PagedResponseModel<Classes> pagedResponse = PagedResponseModel.<Classes>builder()
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .listData(classesList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }
    @PutMapping("/change_class_description/{classId}")
    public ResponseEntity<ResponseModel<?>> updateClassDescription(
            @RequestBody @Valid UpdateClassRequest updateClassRequest,
            BindingResult bindingResult,
            @PathVariable String classId) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ResponseModel.<String>builder()
                            .message("Cập nhật không thành công")
                            .data(ValidationUtils.getValidationErrors(bindingResult))
                            .build());
        }

        classService.updateClass(classId, updateClassRequest);

        return ResponseEntity.ok(ResponseModel.<String>builder()
                .message("Cập nhật thông tin lớp học thành công")
                .data(null)
                .build());
    }


    @GetMapping("/class_detail/{classId}")
    public ResponseEntity<ResponseModel<?>> getClassDetail(@PathVariable String classId) {
        ClassDetailResponse classDetailResponse = classService.getClassDetailById(classId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseModel.<ClassDetailResponse>builder()
                        .message("Lấy dữ liệu lớp có id: " + classId)
                        .data(classDetailResponse)
                        .build());
    }


    @DeleteMapping("/delete_class/{classId}")
    public ResponseEntity<String> deleteClass(@PathVariable String classId) {
        try {
            classService.deleteClass(classId);
            return ResponseEntity.ok("Class deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}

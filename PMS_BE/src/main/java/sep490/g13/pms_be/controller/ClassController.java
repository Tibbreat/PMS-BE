package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.exception.handler.GlobalExceptionHandler;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.classes.AddClassRequest;
import sep490.g13.pms_be.model.request.classes.UpdateClassRequest;
import sep490.g13.pms_be.model.response.classes.ClassListResponse;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.model.response.classes.ClassOption;
import sep490.g13.pms_be.model.response.user.TeacherOfClassResponse;
import sep490.g13.pms_be.service.entity.*;
import sep490.g13.pms_be.utils.ValidationUtils;

import java.util.List;

@RestController
@RequestMapping("/pms/classes")
public class ClassController {
    @Autowired
    private ClassService classService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChildrenService childrenService;
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ClassTeacherService classTeacherService;
    private GlobalExceptionHandler globalExceptionHandler;


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

        try {
            Classes savedClass = classService.createNewClass(classRequest);
            String savedClassId = savedClass.getId();
            classTeacherService.addTeacherIntoClass(savedClassId, classRequest.getTeacherId());
            return ResponseEntity.ok(
                    ResponseModel.builder()
                            .message("Thêm lớp học thành công")
                            .data(classRequest)
                            .build()
            );
        } catch (DataNotFoundException | PermissionNotAcceptException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ResponseModel.builder()
                            .message(e.getMessage())
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
    public ResponseEntity<PagedResponseModel<ClassListResponse>> getClasses(
            @RequestParam int page,
            @RequestParam(required = false) Integer schoolYear,
            @RequestParam(required = false) String ageRange,
            @RequestParam(required = false) String managerId
    ) {
        int size = 10;
        Page<ClassListResponse> result = classService.getClasses(schoolYear, ageRange, managerId, page - 1, size);

        List<ClassListResponse> classList = result.getContent();

        // Tạo response với phân trang
        PagedResponseModel<ClassListResponse> pagedResponse = PagedResponseModel.<ClassListResponse>builder()
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .listData(classList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }
    @PutMapping("/change-class-description/{classId}")
    public ResponseEntity<ResponseModel<String>> updateClassDescription(
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
        try{
        classService.updateClass(classId, updateClassRequest);

        return ResponseEntity.ok(ResponseModel.<String>builder()
                .message("Cập nhật thông tin lớp học thành công")
                .data(null)
                .build());}
        catch (Exception e) {
            return globalExceptionHandler.handleDataNotFoundException(e);
        }
    }

    @PutMapping("/change-class-status/{classId}")
    public ResponseEntity<String> changStatusClass(@PathVariable String classId) {
        try {
            classService.changeStatusClass(classId);
            return ResponseEntity.ok("Cập nhật thông tin lớp thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/class/teacher/{teacherId}")
    public ResponseEntity<PagedResponseModel<Classes>> getClassByTeacherId(
            @PathVariable String teacherId,
            @RequestParam int page){
    int size = 10;
    Page<Classes> results = classTeacherService.getClassByTeacherId(teacherId, size, page - 1);
    List<Classes> classes = results.getContent();
    String msg = classes.isEmpty() ? "Không có dữ liệu" : "Tìm thấy" + results.getTotalElements() + "dữ liệu";

        PagedResponseModel<Classes> pagedResponse = PagedResponseModel.<Classes>builder()
                .page(page)
                .size(size)
                .msg(msg)
                .total(results.getTotalElements())
                .listData(classes)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ResponseModel<?>> getClassById(@PathVariable String classId) {
        Classes classes = classService.getClassById(classId);
        return ResponseEntity.ok(ResponseModel.<Classes>builder()
                .message("Lấy thông tin lớp học thành công")
                .data(classes)
                .build());
    }

    @GetMapping("/teacher/class/{classId}")
    public ResponseEntity<ResponseModel<?>> getTeacherOfClass(@PathVariable String classId) {
        List<TeacherOfClassResponse> teachers = classService.getTeachersOfClass(classId);
        return ResponseEntity.ok(ResponseModel.<List<TeacherOfClassResponse>>builder()
                .message("Lấy thông tin giáo viên của lớp học thành công")
                .data(teachers)
                .build());
    }
    @GetMapping("/class-option")
    public ResponseEntity<ResponseModel<?>> getClassesByTeacherOrManagerId(
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String managerId) {
        List<ClassOption> classes = classService.getClassesByTeacherIdOrManagerId(teacherId, managerId);
        return ResponseEntity.ok(ResponseModel.<List<ClassOption>>builder()
                .message("Lấy danh sách lớp thành công")
                .data(classes)
                .build());
    }
}

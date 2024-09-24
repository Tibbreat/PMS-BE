package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.classes.UpdateClassRequest;
import sep490.g13.pms_be.model.response.classes.ClassDetailResponse;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.LocalDateUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClassService {
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private LocalDateUtils dateUtils;

    public List<Classes> findAll(){
        return classRepo.findAll();
    }
    public Classes addClass(Classes c){
        // Check User co ton tai hay khong
        if (c.getCreatedBy()== null) {
            throw new IllegalArgumentException("CreatedBy field is null or invalid");
        }

//         Fetch the user from the database based on the createdBy ID
        User createdByUser = userRepo.findById(c.getCreatedBy())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + c.getCreatedBy()));

        // Set the createdBy field in the class entity
        c.setCreatedBy(createdByUser.getId());

        // Ensure that only INTERVIEWER or ADMIN role can create a class
        if (createdByUser.getRole() != RoleEnums.ADMIN) {
            throw new PermissionNotAcceptException("Cant create class with other role");
        }
        // Kiểm tra User (Manager) có tồn tại hay không
        Optional<User> managerOpt = userRepo.findById(c.getManager().getId());
        if (managerOpt.isEmpty()) {
            throw new DataNotFoundException("Manager doesn't exist");
        }


        for (ClassTeacher teacher : c.getTeachers()) {
            Optional<User> teacherOpt = userRepo.findById(teacher.getTeacherId().getId());
            if (teacherOpt.isEmpty()) {
                throw new DataNotFoundException("Teacher doesn't exist: " + teacher.getTeacherId().getId());
            }
        }
        LocalDate openingDay = dateUtils.convertToLocalDate(c.getOpeningDay());
        LocalDate closingDay = dateUtils.convertToLocalDate(c.getClosingDay());
        LocalDate today = LocalDate.now();

        // Check if openingDay is after today
        if (!openingDay.isAfter(today)) {
            throw new IllegalArgumentException("Opening Day must be after today");
        }

        // Check if closingDay is at least 6 months after openingDay
        if (closingDay.isBefore(openingDay.plusMonths(6))) {
            throw new IllegalArgumentException("Closing Day must be after Opening Day 6 months");
        }

        // Lưu lớp học vào database
        return classRepo.save(c);
    }

    public Page<Classes> getClasses(Integer schoolYear, String ageRange, String managerId, int page, int size) {
        // Tạo Pageable để phân trang
        Pageable pageable = PageRequest.of(page, size);
        // Gọi repository để lấy danh sách lớp học theo bộ lọc
        return classRepo.findClassesByFilters(schoolYear, ageRange, managerId, pageable);
    }
    @Transactional
    public void updateClass(String classId, UpdateClassRequest updateClassRequest) {
        Classes existingClass = classRepo.findById(classId)
                .orElseThrow(() -> new DataNotFoundException("Class not found with id: " + classId));

        existingClass.setOpeningDay(updateClassRequest.getOpeningDay());
        existingClass.setClosingDay(updateClassRequest.getClosingDay());

        // Cập nhật manager
        User manager = userRepo.findById(updateClassRequest.getManagerId())
                .orElseThrow(() -> new DataNotFoundException("Manager not found with id: " + updateClassRequest.getManagerId()));
        existingClass.setManager(manager);

        existingClass.setLastModifiedBy(updateClassRequest.getLastModifyById());

        classRepo.save(existingClass);
    }


    public void deleteClass(String classId) {
        Classes clazz = classRepo.findById(classId)
                .orElseThrow(() -> new DataNotFoundException("Class not found"));
        LocalDate openingDay = dateUtils.convertToLocalDate(clazz.getOpeningDay());
        LocalDate closingDay = dateUtils.convertToLocalDate(clazz.getClosingDay());
        // Check if the opening day is in the future
        if (openingDay.isAfter(LocalDate.now()) || closingDay.isBefore(LocalDate.now())) {
            classRepo.delete(clazz);
        } else {
            throw new PermissionNotAcceptException("Cannot delete class");
        }
    }
    public ClassDetailResponse getClassDetailById(String id) {
        // Lấy thông tin class từ repository
        Classes classes = classRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học với id " + id));

        // Chuyển đổi đối tượng Classes thành ClassDetailResponse
        return ClassDetailResponse.builder()
                .className(classes.getClassName())
                .ageRange(classes.getAgeRange())
                .openingDay(classes.getOpeningDay())
                .closingDay(classes.getClosingDay())
                .children(classes.getChildren().stream()
                        .map(child -> child.getChildName()) // Giả sử Children có trường getChildName()
                        .collect(Collectors.toSet()))
                .teachers(classes.getTeachers().stream()
                        .map(teacher -> teacher.getTeacherId().getFullName()) // Lấy tên giáo viên từ ClassTeacher
                        .collect(Collectors.toSet()))
                .build();
    }
    public Classes getClassById(String id){
        return classRepo.findById(id).get();
    }

}

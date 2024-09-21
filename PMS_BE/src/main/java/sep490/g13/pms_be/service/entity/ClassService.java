package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.classes.UpdateClassRequest;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.LocalDateUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
            throw new PermissionNotAcceptException("Không được tạo class bằng các role khác");
        }
        // Kiểm tra User (Manager) có tồn tại hay không
        Optional<User> managerOpt = userRepo.findById(c.getManager().getId());
        if (managerOpt.isEmpty()) {
            throw new DataNotFoundException("Manager không tồn tại");
        }


        for (ClassTeacher teacher : c.getTeachers()) {
            Optional<User> teacherOpt = userRepo.findById(teacher.getTeacherId().getId());
            if (teacherOpt.isEmpty()) {
                throw new DataNotFoundException("Giáo viên không tồn tại: " + teacher.getTeacherId().getId());
            }
        }
        LocalDate openingDay = dateUtils.convertToLocalDate(c.getOpeningDay());
        LocalDate closingDay = dateUtils.convertToLocalDate(c.getClosingDay());
        LocalDate today = LocalDate.now();

        // Check if openingDay is after today
        if (!openingDay.isAfter(today)) {
            throw new IllegalArgumentException("Opening Day phải sau ngày hiện tại");
        }

        // Check if closingDay is at least 6 months after openingDay
        if (closingDay.isBefore(openingDay.plusMonths(6))) {
            throw new IllegalArgumentException("Closing Day phải cách Opening Day ít nhất 6 tháng");
        }

        // Lưu lớp học vào database
        return classRepo.save(c);
    }

    public Page<Classes> getClasses(Integer schoolYear, String ageRange, Long managerId, int page, int size) {
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


}

package sep490.g13.pms_be.service.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.classes.AddClassRequest;
import sep490.g13.pms_be.model.request.classes.UpdateClassRequest;
import sep490.g13.pms_be.model.response.classes.ClassListResponse;
import sep490.g13.pms_be.model.response.classes.ClassDetailResponse;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.LocalDateUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassService {
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private LocalDateUtils dateUtils;

    public Classes createNewClass(AddClassRequest classRequest) {
        Classes newClass = new Classes();
        BeanUtils.copyProperties(classRequest, newClass);
        Set<ClassTeacher> teachers = processTeachers(newClass, classRequest.getTeacherId());
        User manager = validateManager(classRequest.getManagerId());
        newClass.setTeachers(teachers);
        newClass.setManager(manager);
        validateClassDates(newClass.getOpeningDay(), newClass.getClosingDay());
        validateCreatedBy(classRequest.getCreatedBy());
        return classRepo.save(newClass);
    }

    private Set<ClassTeacher> processTeachers(Classes newClass, List<String> teacherIds) {
        Set<ClassTeacher> teachers = new HashSet<>();
        for (String teacherId : teacherIds) {
            User teacher = userRepo.getById(teacherId);
            if (teacher == null) {
                throw new DataNotFoundException("Giáo viên không tồn tại: " + teacherId);
            }
            ClassTeacher classTeacher = new ClassTeacher();
            classTeacher.setSchoolClasses(newClass);
            classTeacher.setTeacherId(teacher);
            teachers.add(classTeacher);
        }
        return teachers;
    }

    private User validateManager(String managerId) {
        User manager = userRepo.getById(managerId);
        if (manager == null) {
            throw new DataNotFoundException("Quản lý không tồn tại: " + managerId);
        }

        if (!manager.getRole().equals(RoleEnums.CLASS_MANAGER)) {
            throw new PermissionNotAcceptException("Người này không có vai trò là Quản lý lớp (Class_Manager)");
        }

        return manager;
    }

    private void validateCreatedBy(String createdBy) {
        if (createdBy == null) {
            throw new IllegalArgumentException("CreatedBy field is null or invalid");
        }

        User createdByUser = userRepo.findById(createdBy)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + createdBy));

        if (createdByUser.getRole() != RoleEnums.ADMIN) {
            throw new PermissionNotAcceptException("Cant create class with other role");
        }
    }

    private void validateClassDates(Date openingDay, Date closingDay) {
        LocalDate openDay = dateUtils.convertToLocalDate(openingDay);
        LocalDate closeDay = dateUtils.convertToLocalDate(closingDay);
        LocalDate today = LocalDate.now();

        if (!openDay.isAfter(today)) {
            throw new IllegalArgumentException("Opening Day must be after today");
        }

        if (closeDay.isBefore(openDay.plusMonths(6))) {
            throw new IllegalArgumentException("Closing Day must be at least 6 months after Opening Day");
        }
    }

    public Page<ClassListResponse> getClasses(Integer schoolYear, String ageRange, String managerId, int page, int size) {
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
        User lastMofifyBy = userRepo.findById(updateClassRequest.getManagerId()).get();
        if(lastMofifyBy.getRole() != RoleEnums.ADMIN) {
            throw new PermissionNotAcceptException("Cant update class with other role");
        }else {
            existingClass.setLastModifiedBy(updateClassRequest.getLastModifyById());
        }
        classRepo.save(existingClass);
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
                        .map(Children::getChildName)
                        .collect(Collectors.toSet()))
                .teachers(classes.getTeachers().stream()
                        .map(teacher -> teacher.getTeacherId().getFullName())
                        .collect(Collectors.toSet()))
                .build();
    }
    public Classes getClassById(String id){
        return classRepo.findById(id).get();
    }
    public void changeStatusClass(String classId) {
        Classes clazz = classRepo.findById(classId)
                .orElseThrow(() -> new DataNotFoundException("Class not found"));
        LocalDate openingDay = dateUtils.convertToLocalDate(clazz.getOpeningDay());
        LocalDate closingDay = dateUtils.convertToLocalDate(clazz.getClosingDay());

        // Check if the opening day is in the future and update status accordingly
        if (openingDay.isBefore(LocalDate.now()) || closingDay.isAfter(LocalDate.now())) {
            clazz.setStatus(true);  // Active
        } else {
            throw new PermissionNotAcceptException("Cannot change class status");
        }
        classRepo.save(clazz);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void closeClasses() {
        LocalDate today = LocalDate.now();
        List<Classes> classesToClose = classRepo.findByClosingDayBeforeAndStatus(today, true); // true for active classes

        classesToClose.forEach(cls -> cls.setStatus(false));  // Set status to inactive
        classRepo.saveAll(classesToClose);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void openClasses() {
        LocalDate today = LocalDate.now();
        List<Classes> classesToOpen = classRepo.findByOpeningDayAfterAndStatus(today, false); // false for deactive classes

        classesToOpen.forEach(cls -> cls.setStatus(true));  // Set status to active
        classRepo.saveAll(classesToOpen);
    }

}

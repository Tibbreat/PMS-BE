package sep490.g13.pms_be.service.entity;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.classes.AddClassRequest;
import sep490.g13.pms_be.model.request.classes.UpdateClassRequest;
import sep490.g13.pms_be.model.response.classes.ClassListResponse;
import sep490.g13.pms_be.model.response.classes.ClassOption;
import sep490.g13.pms_be.model.response.user.TeacherOfClassResponse;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.ClassTeacherRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.ExcelUtils;
import sep490.g13.pms_be.utils.LocalDateUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    @Autowired
    private ClassTeacherRepo classTeacherRepo;

    public Classes createNewClass(AddClassRequest classRequest) {
        Classes newClass = new Classes();
        BeanUtils.copyProperties(classRequest, newClass);
        User manager = validateManager(classRequest.getManagerId());
        newClass.setManager(manager);
        validateClassDates(newClass.getOpeningDay(), newClass.getClosingDay());
        validateCreatedBy(classRequest.getCreatedBy());
        return classRepo.save(newClass);
    }

    public Set<ClassTeacher> processTeachers(Classes newClass, List<String> teacherIds) {
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

    public User validateManager(String managerId) {
        User manager = userRepo.getById(managerId);
        if (manager == null) {
            throw new DataNotFoundException("Quản lý không tồn tại: " + managerId);
        }

        if (!manager.getRole().equals(RoleEnums.CLASS_MANAGER)) {
            throw new PermissionNotAcceptException("Người này không có vai trò là Quản lý lớp (Class_Manager)");
        }

        return manager;
    }

    public void validateCreatedBy(String createdBy) {
        if (createdBy == null) {
            throw new IllegalArgumentException("CreatedBy field is null or invalid");
        }

        User createdByUser = userRepo.findById(createdBy)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + createdBy));

        if (createdByUser.getRole() != RoleEnums.ADMIN) {
            throw new PermissionNotAcceptException("Cant create class with other role");
        }
    }

    public void validateClassDates(Date openingDay, Date closingDay) {
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

        if (updateClassRequest.getTeacherId() != null && !updateClassRequest.getTeacherId().isEmpty()) {
            // Xóa danh sách giáo viên cũ
            classTeacherRepo.deleteBySchoolClasses(existingClass);

            // Thêm giáo viên mới
            for (String teacherId : updateClassRequest.getTeacherId()) {
                User teacher = userRepo.findById(teacherId)
                        .orElseThrow(() -> new DataNotFoundException("Teacher not found with id: " + teacherId));

                // Tạo đối tượng ClassTeacher mới
                ClassTeacher classTeacher = ClassTeacher.builder()
                        .schoolClasses(existingClass)
                        .teacherId(teacher)
                        .build();

                // Lưu vào repository
                classTeacherRepo.save(classTeacher);
            }
        }
        User lastMofifyBy = userRepo.findById(updateClassRequest.getLastModifyById()).get();
        if(lastMofifyBy.getRole() == RoleEnums.ADMIN) {
            existingClass.setLastModifiedBy(updateClassRequest.getLastModifyById());
        }else {
            throw new PermissionNotAcceptException("Cant update class with other role");
        }
        classRepo.save(existingClass);
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
            clazz.setStatus(false);
        }
        classRepo.save(clazz);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void closeClasses() {
        LocalDate today = LocalDate.now();
        List<Classes> classesToClose = classRepo.findByClosingDayBeforeAndStatus(today, true);

        classesToClose.forEach(cls -> cls.setStatus(false));  // Set status to inactive
        classRepo.saveAll(classesToClose);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void openClasses() {
        LocalDate today = LocalDate.now();
        List<Classes> classesToOpen = classRepo.findByOpeningDayAfterAndStatus(today, false);

        classesToOpen.forEach(cls -> cls.setStatus(true));  // Set status to active
        classRepo.saveAll(classesToOpen);
    }
    public List<TeacherOfClassResponse> getTeachersOfClass(String classId) {
        return classRepo.getTeacherOfClass(classId);
    }
    public List<ClassOption> getClassesByTeacherIdOrManagerId(String teacherId, String managerId) {
        List<Classes> classesList = classRepo.findClassesByTeacherIdOrManagerId(teacherId, managerId);
        return classesList.stream()
                .map(cls -> new ClassOption(cls.getId(), cls.getClassName()))
                .collect(Collectors.toList());
    }

    public ByteArrayInputStream exportClassToExcel() throws IOException {
        String[] cols = {"Mã lớp", "Tên lớp", "Độ tuổi", "Ngày mở lớp", "Ngày kết thúc", "Trạng thái"};

        // Định dạng ngày theo định dạng dd-MM-yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Lấy tất cả danh sách các lớp học từ database
        List<Classes> data = classRepo.findAll();

        // Sử dụng ExcelUtils để tạo file Excel
        return ExcelUtils.dataToExcel(data, cols, "Danh sách lớp học", (Row row, Classes classes) -> {
            row.createCell(0).setCellValue(classes.getId()); // Mã lớp
            row.createCell(1).setCellValue(classes.getClassName()); // Tên lớp
            row.createCell(2).setCellValue(classes.getAgeRange()); // Độ tuổi
            row.createCell(3).setCellValue(dateFormat.format(classes.getOpeningDay())); // Ngày mở lớp (dd-MM-yyyy)
            row.createCell(4).setCellValue(dateFormat.format(classes.getClosingDay())); // Ngày kết thúc (dd-MM-yyyy)
            row.createCell(5).setCellValue(classes.isStatus() ? "Active" : "Inactive"); // Trạng thái
        });
    }




}

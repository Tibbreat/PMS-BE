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
import sep490.g13.pms_be.repository.SchoolRepo;
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
    @Autowired
    private SchoolRepo schoolRepo;

    public Classes createNewClass(AddClassRequest classRequest) {
        Classes newClass = new Classes();
        BeanUtils.copyProperties(classRequest, newClass);
        User manager = validateManager(classRequest.getManagerId());
        newClass.setManager(manager);
        validateCreatedBy(classRequest.getCreatedBy());
        newClass.setSchool(schoolRepo.findById(classRequest.getSchoolId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy trường học với id: " + classRequest.getSchoolId())));
        return classRepo.save(newClass);
    }


    public User validateManager(String managerId) {
        User manager = userRepo.findById(managerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người quản lý với id: " + managerId));

        if (!manager.getRole().equals(RoleEnums.CLASS_MANAGER)) {
            throw new PermissionNotAcceptException("Người này không có vai trò là Quản lý lớp (Class_Manager)");
        }

        return manager;
    }

    public void validateCreatedBy(String createdBy) {
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




    public Classes getClassById(String id) {
        return classRepo.findById(id).get();
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
            row.createCell(5).setCellValue(classes.isStatus() ? "Active" : "Inactive"); // Trạng thái
        });
    }


}

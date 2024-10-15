package sep490.g13.pms_be.service.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.classes.AddClassRequest;
import sep490.g13.pms_be.model.response.classes.ClassListResponse;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.LocalDateUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ClassServiceTest {
    @Mock
    private ClassRepo classRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private LocalDateUtils dateUtils;
    @InjectMocks
    private ClassService classService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User user1 = User.builder().role(RoleEnums.ADMIN).build();
    }

    @Test
    void validateManager_WhenManagerExists() {  //Test validate Manager
        // Given
        String managerId = "2";
        User mockManager = new User();
        mockManager.setRole(RoleEnums.CLASS_MANAGER);
        when(userRepo.getById(managerId)).thenReturn(mockManager);

        // When
        User result = classService.validateManager(managerId);

        // Then
        assertNotNull(result);
        assertEquals(RoleEnums.CLASS_MANAGER, result.getRole());
    }

    @Test
    void validateManager_ShouldThrowDataNotFoundException_WhenManagerDoesNotExist() { //Test User không tồn tại
        // Given
        String managerId = "non_existent_manager";
        when(userRepo.getById(managerId)).thenReturn(null); // Giả lập manager không tồn tại

        // When / Then
        assertThrows(DataNotFoundException.class, () -> {
            classService.validateManager(managerId);
        });
    }

    @Test
    void validateManager_ShouldThrowPermissionNotAcceptException_WhenManagerRoleIsNotClassManager() { //Test User không có Role CLass_Manager
        // Given
        String managerId = "existing_manager";
        User manager = new User();
        manager.setRole(RoleEnums.TEACHER); // Sai role
        when(userRepo.getById(managerId)).thenReturn(manager);

        // When / Then
        assertThrows(PermissionNotAcceptException.class, () -> {
            classService.validateManager(managerId);
        });
    }

    @Test
    void createNewClass_ShouldThrowExceptionIfManagerNotFound() { //Test tạo lớp không có Manager
        // Given
        AddClassRequest request = new AddClassRequest();
        request.setManagerId("3");
        when(userRepo.getById(anyString())).thenReturn(null);

        // When/Then
        assertThrows(NullPointerException.class, () -> classService.createNewClass(request));
    }

    @Test
    void createNewClass_ShouldThrowExceptionIfClosingDayIsLessThanSixMonthsAfterOpeningDay() { // Test tạo lớp không thành công Closing day không cách opening day ít nhất 6 tháng
        // Given
        AddClassRequest request = new AddClassRequest();
        request.setManagerId("2");
        request.setOpeningDay(new Date());
        request.setClosingDay(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)); // Ngày đóng nhỏ hơn 6 tháng
// When
        User manager = new User();
        manager.setRole(RoleEnums.CLASS_MANAGER);
        when(userRepo.getById("2")).thenReturn(manager);
        User teacher = new User();
        teacher.setRole(RoleEnums.TEACHER);
        when(userRepo.getById("5")).thenReturn(teacher);
        LocalDate openingLocalDate = LocalDate.now();
        LocalDate closingLocalDate = LocalDate.now().plusDays(30); // Chỉ 30 ngày
        when(dateUtils.convertToLocalDate(any(Date.class))).thenReturn(openingLocalDate, closingLocalDate);

        //Then
        assertThrows(IllegalArgumentException.class, () -> classService.createNewClass(request));
    }
    @Test
    void createNewClass_ShouldAddClassSuccessfully() { // Test tạo lớp thành công
        // Given
        AddClassRequest request = new AddClassRequest();
        Date openingDate = new Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000); // Ngày mai
        Date closingDate = new Date(System.currentTimeMillis() + 190L * 24 * 60 * 60 * 1000); // Ngày đóng lớn hơn 6 tháng // Ngày đóng lớn hơn 6 tháng
        request.setOpeningDay(openingDate);
        request.setClosingDay(closingDate);
        request.setManagerId("2");
        request.setCreatedBy("1");

        // Mock the conversion of Date to LocalDate
        LocalDate openingLocalDate = LocalDate.now().plusDays(1); // Ngày mở lớp hiện tại
        LocalDate closingLocalDate = openingLocalDate.plusDays(190); // Ngày đóng lớp (lớn hơn 6 tháng)

        // Mock the correct conversion for each date
        when(dateUtils.convertToLocalDate(openingDate)).thenReturn(openingLocalDate);
        when(dateUtils.convertToLocalDate(closingDate)).thenReturn(closingLocalDate);

        // When
        User manager = new User();
        manager.setRole(RoleEnums.CLASS_MANAGER);
        when(userRepo.getById("2")).thenReturn(manager);

        User teacher = new User();
        teacher.setRole(RoleEnums.TEACHER);
        when(userRepo.getById("5")).thenReturn(teacher);
        User admin = new User();
        admin.setRole(RoleEnums.ADMIN);
        when(userRepo.findById("1")).thenReturn(Optional.of(admin));
        // Call the service method
        classService.createNewClass(request);

        // Then
        verify(classRepo, times(1)).save(any(Classes.class)); // Kiểm tra class được lưu thành công
    }

    @Test
    void createNewClass_ShouldThrowExceptionIfManagerNotClassManager() { // Test tạo lớp không thành công sai role của Manager
        // Given
        AddClassRequest request = new AddClassRequest();
        Date openingDate = new Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000); // Ngày mai
        Date closingDate = new Date(System.currentTimeMillis() + 190L * 24 * 60 * 60 * 1000); // Ngày đóng lớn hơn 6 tháng
        request.setOpeningDay(openingDate);
        request.setClosingDay(closingDate);
        request.setManagerId("2");
        request.setCreatedBy("1");


        // Mock the conversion of Date to LocalDate
        LocalDate openingLocalDate = LocalDate.now().plusDays(1);
        LocalDate closingLocalDate = openingLocalDate.plusDays(190);

        when(dateUtils.convertToLocalDate(openingDate)).thenReturn(openingLocalDate);
        when(dateUtils.convertToLocalDate(closingDate)).thenReturn(closingLocalDate);

        // Mock users with incorrect manager role
        User manager = new User();
        manager.setRole(RoleEnums.TEACHER); // Sai role, không phải CLASS_MANAGER
        when(userRepo.getById("2")).thenReturn(manager);

        User teacher = new User();
        teacher.setRole(RoleEnums.TEACHER);
        when(userRepo.getById("5")).thenReturn(teacher);

        User admin = new User();
        admin.setRole(RoleEnums.ADMIN);
        when(userRepo.findById("1")).thenReturn(Optional.of(admin));

        // Then
        assertThrows(PermissionNotAcceptException.class, () -> classService.createNewClass(request), "Manager role is invalid");
    }
    @Test
    void createNewClass_ShouldThrowExceptionIfCreatedByIsNotAdmin() { // Tạo lớp không thành công, createdBy phải là Admin
        // Given
        AddClassRequest request = new AddClassRequest();
        Date openingDate = new Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000); // Ngày mai
        Date closingDate = new Date(System.currentTimeMillis() + 190L * 24 * 60 * 60 * 1000); // Ngày đóng lớn hơn 6 tháng
        request.setOpeningDay(openingDate);
        request.setClosingDay(closingDate);
        request.setManagerId("2");
        request.setCreatedBy("1");


        // Mock the conversion of Date to LocalDate
        LocalDate openingLocalDate = LocalDate.now().plusDays(1);
        LocalDate closingLocalDate = openingLocalDate.plusDays(190);

        when(dateUtils.convertToLocalDate(openingDate)).thenReturn(openingLocalDate);
        when(dateUtils.convertToLocalDate(closingDate)).thenReturn(closingLocalDate);

        // Mock users
        User manager = new User();
        manager.setRole(RoleEnums.CLASS_MANAGER);
        when(userRepo.getById("2")).thenReturn(manager);

        User teacher = new User();
        teacher.setRole(RoleEnums.TEACHER);
        when(userRepo.getById("5")).thenReturn(teacher);

        User nonAdmin = new User();
        nonAdmin.setRole(RoleEnums.TEACHER); // Sai role, không phải ADMIN
        when(userRepo.findById("1")).thenReturn(Optional.of(nonAdmin));

        // Then
        assertThrows(PermissionNotAcceptException.class, () -> classService.createNewClass(request), "Created by user is not ADMIN");
    }
    @Test
    void createNewClass_ShouldThrowExceptionIfOpeningDayIsInPast() { // Tạo lớp không thành công, opening day trước ngày hôm nay
        // Given
        AddClassRequest request = new AddClassRequest();
        Date openingDate = new Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000); // Ngày mở lớp là hôm qua
        Date closingDate = new Date(System.currentTimeMillis() + 190L * 24 * 60 * 60 * 1000); // Ngày đóng lớn hơn 6 tháng
        request.setOpeningDay(openingDate);
        request.setClosingDay(closingDate);
        request.setManagerId("2");
        request.setCreatedBy("1");


        // Mock the conversion of Date to LocalDate
        LocalDate openingLocalDate = LocalDate.now().minusDays(1); // Hôm qua
        LocalDate closingLocalDate = openingLocalDate.plusDays(190);

        when(dateUtils.convertToLocalDate(openingDate)).thenReturn(openingLocalDate);
        when(dateUtils.convertToLocalDate(closingDate)).thenReturn(closingLocalDate);

        // Mock users
        User manager = new User();
        manager.setRole(RoleEnums.CLASS_MANAGER);
        when(userRepo.getById("2")).thenReturn(manager);

        User teacher = new User();
        teacher.setRole(RoleEnums.TEACHER);
        when(userRepo.getById("5")).thenReturn(teacher);

        User admin = new User();
        admin.setRole(RoleEnums.ADMIN);
        when(userRepo.findById("1")).thenReturn(Optional.of(admin));

        // Then
        assertThrows(IllegalArgumentException.class, () -> classService.createNewClass(request), "Opening day is in the past");
    }

    @Test
    void getClasses_ShouldReturnPagedClasses() { // Test lấy pagination của classes
        // Given
        int page = 0; // trang đầu tiên
        int size = 10; // số lượng lớp mỗi trang
        Integer schoolYear = 2024;
        String ageRange = "3-5";
        String managerId = "2";

        // Tạo danh sách các lớp học giả lập
        List<ClassListResponse> classes = new ArrayList<>();
        ClassListResponse class1 = new ClassListResponse("1", "Class A", ageRange, new Date(System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000), new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000), managerId, "Trang", true);
        ClassListResponse class2 = new ClassListResponse("2", "Class B", ageRange, new Date(System.currentTimeMillis() + 70 * 24 * 60 * 60 * 1000), new Date(System.currentTimeMillis() + 20 * 24 * 60 * 60 * 1000), managerId,"Hòa", true);
        classes.add(class1);
        classes.add(class2);

        // Tạo Page chứa danh sách lớp học
        Page<ClassListResponse> classPage = new PageImpl<>(classes, PageRequest.of(page, size), classes.size());

        // Mock repository trả về danh sách lớp học đã phân trang
        when(classRepo.findClassesByFilters(schoolYear, ageRange, managerId, PageRequest.of(page, size))).thenReturn(classPage);

        // When
        Page<ClassListResponse> result = classService.getClasses(schoolYear, ageRange, managerId, page, size);

        // Then
        System.out.println(result.getContent());
        assertNotNull(result);
        assertEquals(2, result.getContent().size()); // Kiểm tra số lượng lớp học trong kết quả
        assertEquals(class1.getId(), result.getContent().get(0).getId()); // Kiểm tra lớp học đầu tiên
        assertEquals(class1.getClassName(), result.getContent().get(0).getClassName());
        assertEquals(class1.getAgeRange(), result.getContent().get(0).getAgeRange());
        assertEquals(class1.getOpeningDay(), result.getContent().get(0).getOpeningDay());
        assertEquals(class1.getClosingDay(), result.getContent().get(0).getClosingDay());
        assertEquals(class2.getId(), result.getContent().get(1).getId()); // Kiểm tra lớp học thứ hai
        assertEquals(class2.getClassName(), result.getContent().get(1).getClassName());
        assertEquals(class2.getAgeRange(), result.getContent().get(1).getAgeRange());
        assertEquals(class2.getOpeningDay(), result.getContent().get(1).getOpeningDay());
        assertEquals(class2.getClosingDay(), result.getContent().get(1).getClosingDay());

        verify(classRepo, times(1)).findClassesByFilters(schoolYear, ageRange, managerId, PageRequest.of(page, size)); // Kiểm tra repository được gọi một lần
    }
    // Test với chỉ schoolYear filter
    @Test
    void getClasses_ShouldReturnPagedClasses_WithSchoolYearFilter() {
        // Given
        int page = 0; // Đảm bảo rằng chúng ta lấy trang đầu tiên
        int size = 10;
        Integer schoolYear = 2024;
        String ageRange = null;
        String managerId = null;

        List<ClassListResponse> classes = new ArrayList<>();

        // Lớp A có openingDay thuộc năm 2024
        Calendar cal2024 = Calendar.getInstance();
        cal2024.set(2024, Calendar.SEPTEMBER, 17); // Ngày thuộc năm 2024
        Date openingDay2024 = cal2024.getTime();
        System.out.println("Lớp A opening year: " + cal2024.get(Calendar.YEAR)); // In ra năm của lớp A

        ClassListResponse class1 = new ClassListResponse("1", "Class A", "3-5",
                new Date(System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000), // Ngày closing bất kỳ
                openingDay2024, "1", "Trang", true);
        classes.add(class1);

        // Lớp B có openingDay thuộc năm 2025
        Calendar cal2025 = Calendar.getInstance();
        cal2025.set(2025, Calendar.SEPTEMBER, 17); // Ngày thuộc năm 2025
        Date openingDay2025 = cal2025.getTime();
        System.out.println("Lớp B opening year: " + cal2025.get(Calendar.YEAR)); // In ra năm của lớp B

        ClassListResponse class2 = new ClassListResponse("2", "Class B", "3-5",
                new Date(System.currentTimeMillis() + 70 * 24 * 60 * 60 * 1000), // Ngày closing bất kỳ
                openingDay2025, "2", "Hoa", true);
        classes.add(class2);

        // Kết quả phân trang giả lập
        Page<ClassListResponse> classPage = new PageImpl<>(classes, PageRequest.of(page, size), classes.size());

        // Mock repository trả về danh sách lớp học đã phân trang
        when(classRepo.findClassesByFilters(schoolYear, ageRange, managerId, PageRequest.of(page, size)))
                .thenReturn(classPage);

        // When
        Page<ClassListResponse> result = classService.getClasses(schoolYear, ageRange, managerId, page, size);

        // Logging để kiểm tra dữ liệu trả về
        System.out.println("Classes returned from repo: " + result.getContent());

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size()); // Chỉ có 1 lớp thuộc năm 2024
        assertEquals(class1.getId(), result.getContent().get(0).getId()); // Lớp A được trả về

        verify(classRepo, times(1)).findClassesByFilters(schoolYear, ageRange, managerId, PageRequest.of(page, size));
    }


    @Test
    void updateClass() {
    }

    @Test
    void getClassDetailById() {
    }

    @Test
    void getClassById() {
    }

    @Test
    void changeStatusClass() {
    }
}
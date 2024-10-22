package sep490.g13.pms_be.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.handler.GlobalExceptionHandler;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.classes.AddClassRequest;
import sep490.g13.pms_be.model.request.classes.UpdateClassRequest;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.ClassService;
import sep490.g13.pms_be.service.entity.ClassTeacherService;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClassControllerTest {

    @Mock
    private ClassService classService;
    @Mock
    private ClassTeacherService classTeacherService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private GlobalExceptionHandler globalExceptionHandler;
    @InjectMocks
    private ClassController classController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testAddNewClass_Success() {
        // Arrange
        Date openingDay = new Date();
        Date closingDay = new Date();
        AddClassRequest classRequest = new AddClassRequest();
        classRequest.setClassName("Mầm 1");
        classRequest.setAgeRange("1-2");
        classRequest.setOpeningDay(openingDay);
        classRequest.setClosingDay(closingDay);
        classRequest.setTeacherId("4028b2b4926cb0f301926cb1025a0000");
        classRequest.setManagerId("4028b2b4926cb0f301926cb10a830006");
        classRequest.setCreatedBy("4028b2b4926cb0f301926cb109260004");

        when(bindingResult.hasErrors()).thenReturn(false);

        Classes savedClass = new Classes();
        savedClass.setId("newClassId");

        when(classService.createNewClass(any(AddClassRequest.class))).thenReturn(savedClass);

        // Act
        ResponseEntity<ResponseModel<?>> response = classController.addNewClass(classRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Thêm lớp học thành công", response.getBody().getMessage());
        verify(classService, times(1)).createNewClass(any(AddClassRequest.class));
        verify(classTeacherService, times(1)).addTeacherIntoClass(eq("newClassId"), any());
    }


    @Test
    public void testAddNewClass_InvalidClassData() {
        // Arrange
        AddClassRequest classRequest = new AddClassRequest();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        ResponseEntity<ResponseModel<?>> response = classController.addNewClass(classRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Thông tin lớp học không hợp lệ", response.getBody().getMessage());
        verify(classService, never()).createNewClass(any(AddClassRequest.class));
    }

    @Test
    public void testAddNewClass_ManagerNotFound() {
        // Arrange
        AddClassRequest classRequest = new AddClassRequest();
        classRequest.setManagerId("0000");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(classService.createNewClass(any(AddClassRequest.class)))
                .thenThrow(new DataNotFoundException("Quản lý không tồn tại"));

        // Act
        ResponseEntity<ResponseModel<?>> response = classController.addNewClass(classRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Quản lý không tồn tại", response.getBody().getMessage());
    }
    @Test
    public void testAddNewClass_ManagerRoleNotValid() {
        // Arrange
        AddClassRequest classRequest = new AddClassRequest();
        classRequest.setManagerId("2c96a0819274789901927479455b0000");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(classService.createNewClass(any(AddClassRequest.class)))
                .thenThrow(new PermissionNotAcceptException("Người này không có vai trò là Quản lý lớp (Class_Manager)"));

        // Act
        ResponseEntity<ResponseModel<?>> response = classController.addNewClass(classRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Người này không có vai trò là Quản lý lớp (Class_Manager)", response.getBody().getMessage());
    }
    @Test
    public void testAddNewClass_TeacherNotFound() {
        // Arrange
        AddClassRequest classRequest = new AddClassRequest();
        classRequest.setTeacherId("123456");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(classService.createNewClass(any(AddClassRequest.class)))
                .thenThrow(new DataNotFoundException("Giáo viên không tồn tại"));

        // Act
        ResponseEntity<ResponseModel<?>> response = classController.addNewClass(classRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Giáo viên không tồn tại", response.getBody().getMessage());
    }
    @Test
    public void testAddNewClass_CreatedByNotFound() {
        // Arrange
        AddClassRequest classRequest = new AddClassRequest();
        classRequest.setCreatedBy("abc");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(classService.createNewClass(any(AddClassRequest.class)))
                .thenThrow(new DataNotFoundException("Quản lý không tồn tại "));

        // Act
        ResponseEntity<ResponseModel<?>> response = classController.addNewClass(classRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Quản lý không tồn tại ", response.getBody().getMessage());
    }
    @Test
    public void testAddNewClass_CreatedByNotValid() {
        // Arrange
        AddClassRequest classRequest = new AddClassRequest();
        classRequest.setCreatedBy("2c96a0819274789901927479455b0000");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(classService.createNewClass(any(AddClassRequest.class)))
                .thenThrow(new PermissionNotAcceptException("Cant create class with other role "));

        // Act
        ResponseEntity<ResponseModel<?>> response = classController.addNewClass(classRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cant create class with other role ", response.getBody().getMessage());
    }
    @Test
    public void testAddNewClass_OpeningDayNotValid() {
        // Arrange
        Date openingDay = new Date("11/11/2023");
        AddClassRequest classRequest = new AddClassRequest();
        classRequest.setOpeningDay(openingDay);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(classService.createNewClass(any(AddClassRequest.class)))
                .thenThrow(new IllegalArgumentException("Opening Day must be after today"));

        // Act
        ResponseEntity<ResponseModel<?>> response = classController.addNewClass(classRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Opening Day must be after today", response.getBody().getMessage());
    }
    @Test
    public void testAddNewClass_ClosingDayNotValid() {
        // Arrange
        Date openingDay = new Date("11/11/2024");
        Date closingDay = new Date("11/01/2025");
        AddClassRequest classRequest = new AddClassRequest();
        classRequest.setOpeningDay(openingDay);
        classRequest.setClosingDay(closingDay);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(classService.createNewClass(any(AddClassRequest.class)))
                .thenThrow(new IllegalArgumentException("Closing Day must be at least 6 months after Opening Day"));

        // Act
        ResponseEntity<ResponseModel<?>> response = classController.addNewClass(classRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Closing Day must be at least 6 months after Opening Day", response.getBody().getMessage());
    }
    @Test
    public void testUpdateClassDescription_Success() {
        // Arrange
        String classId = "class123";
        UpdateClassRequest updateClassRequest = new UpdateClassRequest();
        updateClassRequest.setOpeningDay(new Date());
        updateClassRequest.setClosingDay(new Date());
        updateClassRequest.setTeacherId(List.of("40280252928ef43901928ef44d4b0002", "40280252928ef43901928ef44cfb0001"));
        updateClassRequest.setManagerId("4028b2b4926cb0f301926cb10a830006");
        updateClassRequest.setLastModifyById("4028b2b4926cb0f301926cb109260004");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        ResponseEntity<ResponseModel<String>> response = classController.updateClassDescription(updateClassRequest, bindingResult, classId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cập nhật thông tin lớp học thành công", response.getBody().getMessage());
        verify(classService, times(1)).updateClass(eq(classId), any(UpdateClassRequest.class));
    }

    @Test
    public void testUpdateClassDescription_InvalidData() {
        // Arrange
        String classId = "class123";
        UpdateClassRequest updateClassRequest = new UpdateClassRequest();

        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        ResponseEntity<ResponseModel<String>> response = classController.updateClassDescription(updateClassRequest, bindingResult, classId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cập nhật không thành công", response.getBody().getMessage());
        verify(classService, never()).updateClass(anyString(), any(UpdateClassRequest.class));
    }

    @Test
    public void testUpdateClassDescription_ClassNotFound() {
        // Arrange
        String classId = "abc";
        UpdateClassRequest updateClassRequest = new UpdateClassRequest();
        updateClassRequest.setManagerId("4028b2b4926cb0f301926cb10a830006");

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataNotFoundException("Class not found with id: " + classId))
                .when(classService).updateClass(eq(classId), any(UpdateClassRequest.class));

        // Act
        ResponseEntity<ResponseModel<String>> response = classController.updateClassDescription(updateClassRequest, bindingResult, classId);

        // Assert
        assertNull(response);
    }

    @Test
    public void testUpdateClassDescription_ManagerNotFound() {
        // Arrange
        String classId = "class123";
        UpdateClassRequest updateClassRequest = new UpdateClassRequest();
        updateClassRequest.setManagerId("2c96a0819274789901927479455b0000");

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataNotFoundException("Manager not found with id: " + updateClassRequest.getManagerId()))
                .when(classService).updateClass(eq(classId), any(UpdateClassRequest.class));
        // Act
        ResponseEntity<ResponseModel<String>> response = classController.updateClassDescription(updateClassRequest, bindingResult, classId);
        // Assert
        assertNull(response);
    }

    @Test
    public void testUpdateClassDescription_TeacherNotFound() {
        // Arrange
        String classId = "class123";
        UpdateClassRequest updateClassRequest = new UpdateClassRequest();
        updateClassRequest.setTeacherId(List.of("123"));

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataNotFoundException("Teacher not found with id: invalidTeacherId"))
                .when(classService).updateClass(eq(classId), any(UpdateClassRequest.class));

        // Act
        ResponseEntity<ResponseModel<String>> response = classController.updateClassDescription(updateClassRequest, bindingResult, classId);

        // Assert
            assertNull(response);

        }

    @Test
    public void testUpdateClassDescription_PermissionNotAccept() {
        // Arrange
        String classId = "class123";
        UpdateClassRequest updateClassRequest = new UpdateClassRequest();
        updateClassRequest.setLastModifyById("2c96a0819274789901927479455b0000");

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new PermissionNotAcceptException("Cant update class with other role"))
                .when(classService).updateClass(eq(classId), any(UpdateClassRequest.class));

        // Act
        ResponseEntity<ResponseModel<String>> response = classController.updateClassDescription(updateClassRequest, bindingResult, classId);

        // Assert
        assertNull(response);
    }


}
package sep490.g13.pms_be.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.jwt.JwtTokenProvider;
import sep490.g13.pms_be.model.request.auth.LoginRequest;
import sep490.g13.pms_be.model.response.AuthResponse;
import sep490.g13.pms_be.model.response.auth.UserDataResponse;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.auth.AuthService;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;
    @Mock
    private HttpServletRequest request;
    private User user;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("kiennt");
        user.setId("2222njfajfbyycai124bt6");
        user.setEmail("kiennt@example.com");
        user.setPhone("0824335589");
        user.setAddress("Thanh Hóa");
        user.setIsActive(true);
        user.setRole(RoleEnums.ADMIN);
    }

    @Test
    public void testLoginSuccess() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("kiennt");
        loginRequest.setPassword("12345678");

        User user = new User();
        user.setUsername("kiennt");
        user.setIsActive(true);
        user.setRole(RoleEnums.ADMIN);

        when(userRepo.findByUsername("kiennt")).thenReturn(user);
        when(authService.login("kiennt", "12345678")).thenReturn("Token");

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bearer", response.getBody().getTokenType());
        assertEquals("Token", response.getBody().getToken());
        assertEquals("Đăng nhập thành công", response.getBody().getMessage());
    }

    @Test
    public void testLoginInactiveUser() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("inactiveUser");
        loginRequest.setPassword("password");

        User user = new User();
        user.setUsername("inactiveUser");
        user.setIsActive(false);
        user.setRole(RoleEnums.TEACHER);

        when(userRepo.findByUsername("inactiveUser")).thenReturn(user);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody().getToken());
        assertEquals("Tài khoản của bạn đã bị hạn chế, liên hệ quản lý để xử lý", response.getBody().getMessage());
    }

    @Test
    public void testLoginUserNotFound() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("123");
        loginRequest.setPassword("ne49h93d");

        when(userRepo.findByUsername("123")).thenReturn(null);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authController.login(loginRequest));
    }

    @Test
    public void testLoginWithNullUsername() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);  // Null username
        loginRequest.setPassword("12345678");

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authController.login(loginRequest));
    }

    @Test
    public void testLoginWithNullPassword() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("kiennt");
        loginRequest.setPassword(null);  // Null password

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authController.login(loginRequest));
    }



    @Test
    public void testGetAccountUserNotFound() {
        // Arrange
        String token = "Bearer token";
        String username = "nonexistentUser";

        when(JwtTokenProvider.getTokenFromRequest(request)).thenReturn(token);
        when(jwtTokenProvider.getUserName(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(DataNotFoundException.class, () -> authController.getAccount(request));
    }

    @Test
    public void testGetAccountInvalidToken() {
        // Arrange
        when(JwtTokenProvider.getTokenFromRequest(request)).thenReturn(null);

        // Act & Assert
        assertThrows(DataNotFoundException.class, () -> authController.getAccount(request));
    }

}

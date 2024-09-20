package sep490.g13.pms_be.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.jwt.JwtTokenProvider;
import sep490.g13.pms_be.model.request.auth.LoginRequest;
import sep490.g13.pms_be.model.response.AuthResponse;
import sep490.g13.pms_be.model.response.auth.UserDataResponse;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.auth.AuthService;

@RestController
@RequestMapping("/pms/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (token == null) {
            throw new BadCredentialsException("Thông tin đăng nhập không chính xác");
        }
        User userData = userRepo.findByUsername(loginRequest.getUsername());
        if (!userData.getIsActive()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(AuthResponse.builder()
                            .role(null)
                            .token(token)
                            .tokenType("Bearer")
                            .build());
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(AuthResponse.builder()
                            .role(userData.getRole().name())
                            .token(token)
                            .tokenType("Bearer")
                            .build());
        }
    }

    @GetMapping("/account")
    public ResponseEntity<UserDataResponse> getAccount(HttpServletRequest request) {
        String token = JwtTokenProvider.getTokenFromRequest(request);

        String username = jwtTokenProvider.getUserName(token);

        User userData = userRepo.findByUsername(username);
        if (userData == null) {
            throw new DataNotFoundException("Người dùng không tồn tại");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserDataResponse.builder()
                        .username(username)
                        .id(userData.getId())
                        .email(userData.getEmail())
                        .phone(userData.getPhone())
                        .address(userData.getAddress())
                        .isActive(userData.getIsActive())
                        .imageLink(userData.getImageLink())
                        .role(userData.getRole().name())
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = JwtTokenProvider.getTokenFromRequest(request);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ");
        }
        // Xác thực token và lấy thông tin người dùng từ token
        String username = jwtTokenProvider.getUserName(token);

        // Nếu không tìm thấy người dùng từ token, trả về thông báo lỗi
        if (username == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể xác thực người dùng");
        }

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        if (auth != null && auth.getName().equals(username)) {
            SecurityContextHolder.clearContext();
        }

        return ResponseEntity.ok("Đăng xuất thành công");
    }
}

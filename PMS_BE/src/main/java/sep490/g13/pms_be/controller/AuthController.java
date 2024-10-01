package sep490.g13.pms_be.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.jwt.JwtTokenProvider;
import sep490.g13.pms_be.model.request.auth.ChangePasswordRequest;
import sep490.g13.pms_be.model.request.auth.LoginRequest;
import sep490.g13.pms_be.model.response.AuthResponse;
import sep490.g13.pms_be.model.response.auth.UserDataResponse;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.auth.AuthService;
import sep490.g13.pms_be.service.entity.UserService;
import sep490.g13.pms_be.utils.StringUtils;

@RestController
@RequestMapping("/pms/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (token == null) {
            throw new BadCredentialsException("Thông tin đăng nhập không chính xác");
        }
        User userData = userRepo.findByUsername(loginRequest.getUsername());
        if (Boolean.FALSE.equals(userData.getIsActive())) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(AuthResponse.builder()
                            .role(null)
                            .token(token)
                            .message("Đăng nhập thành công")
                            .tokenType("Bearer")
                            .build());
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(AuthResponse.builder()
                            .message("Tài khoản của bạn đã bị hạn chế, liên hệ quản lý để xử lý")
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
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseModel<String>> sendCode(@RequestParam String email) {

        // Tìm người dùng trong cơ sở dữ liệu dựa trên email
        User user = userService.findByEmail(email);

        // Nếu không tìm thấy người dùng, ném ngoại lệ DataNotFoundException
        if (user == null) {
            throw new DataNotFoundException("Người dùng có email " + email + " không tồn tại");
        }

        // Nếu tìm thấy người dùng, tạo mật khẩu mới ngẫu nhiên
        String newPassword = StringUtils.randomString(8);
        user.setPassword(passwordEncoder.encode(newPassword));
        System.out.println(newPassword);
        // Lưu mật khẩu mới đã hash vào cơ sở dữ liệu
        userRepo.save(user);

        // Trả về phản hồi với mã trạng thái OK và thông báo rằng đặt lại mật khẩu thành công
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseModel
                        .<String>builder()
                        .message("Reset password success!")
                        .build());
    }
    @PostMapping("/change-password")
    public ResponseEntity<ResponseModel<String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {

        // Tìm người dùng trong cơ sở dữ liệu dựa trên email
        User user = userService.findByEmail(changePasswordRequest.getEmail());

        // Nếu không tìm thấy người dùng, ném ngoại lệ DataNotFoundException
        if (user == null) {
            throw new DataNotFoundException("Người dùng có email " + changePasswordRequest.getEmail() + " không tồn tại");
        }

        // Kiểm tra mật khẩu hiện tại có khớp với mật khẩu đã lưu trong cơ sở dữ liệu không
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .body(ResponseModel
                            .<String>builder()
                            .message("Mật khẩu hiện tại không chính xác")
                            .build());
        }

        // Kiểm tra mật khẩu mới và mật khẩu xác nhận có giống nhau không
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .body(ResponseModel
                            .<String>builder()
                            .message("Mật khẩu mới và mật khẩu xác nhận không giống nhau")
                            .build());
        }

        // Nếu thỏa mãn tất cả điều kiện, hash mật khẩu mới và lưu vào cơ sở dữ liệu
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepo.save(user);

        // Trả về phản hồi với mã trạng thái OK và thông báo rằng thay đổi mật khẩu thành công
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseModel
                        .<String>builder()
                        .message("Đổi mật khẩu thành công")
                        .build());
    }
}

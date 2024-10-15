package sep490.g13.pms_be.model.request.auth;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String username;
    private String password;
}

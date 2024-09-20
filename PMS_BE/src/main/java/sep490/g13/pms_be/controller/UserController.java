package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.model.request.user.AddUserRequest;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.UserService;
import sep490.g13.pms_be.utils.ValidationUtils;

@RestController
@RequestMapping("/pms/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseEntity<ResponseModel<?>> addUser(@RequestBody @Valid AddUserRequest addUserRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ValidationUtils.getValidationErrors(bindingResult);
            return ResponseEntity.badRequest()
                    .body(ResponseModel.<String>builder()
                            .message("Thêm người dùng không thành công")
                            .data(errorMessage)
                            .build());
        }
        User newUser = userService.addUser(addUserRequest);

        String message = newUser == null? "Thêm người dùng không thành công" : "Thêm người dùng thành công";
        HttpStatus responseCode = newUser == null? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(responseCode)
                .body(ResponseModel.<User>builder()
                        .message(message)
                        .data(newUser)
                        .build());
    }
}

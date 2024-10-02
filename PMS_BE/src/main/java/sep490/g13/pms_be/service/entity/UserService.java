package sep490.g13.pms_be.service.entity;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.model.request.user.AddUserRequest;
import sep490.g13.pms_be.model.request.user.UpdateUserNameAndPasswordRequest;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.StringUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User addUser(AddUserRequest request) {
        String defaultPassword = StringUtils.randomString(8);
        String accountName = StringUtils.generateUsername(request.getFullName());
        int count = userRepo.countByUsernameContaining(accountName);
        String username = "";
        if (count == 0) {
            username += accountName;
        } else {
            username += accountName + (count + 1);
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setUsername(username.trim());
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setEmail(username.trim() + "@pms.com");

        return userRepo.save(user);
    }

    public User updateUserNameAndPassword(UpdateUserNameAndPasswordRequest request) {
        // Tìm user dựa trên email (hoặc một định danh duy nhất khác)
        Optional<User> existingUserOpt = userRepo.findByEmail(request.getEmail());


        if (existingUserOpt.isPresent()) {
            // Lấy user hiện tại
            User user = existingUserOpt.get();
            if(user.getUsername() !=null){
            // Nếu fullName có trong request, tạo lại username dựa trên fullName
            if (request.getFullName() != null && !request.getFullName().isEmpty()) {

                String accountName = StringUtils.generateUsername(request.getFullName());
                int count = userRepo.countByUsernameContaining(accountName);
                String username = "";
                if (count == 0) {
                    username += accountName;
                } else {
                    username += accountName + (count + 1);
                }
                user.setUsername(username.trim());
            }
            }

            // Cập nhật password nếu có trong request, hoặc tạo password mặc định
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            } else {
                // Tạo password mặc định nếu không có password trong request
                String defaultPassword = StringUtils.randomString(8);
                user.setPassword(passwordEncoder.encode(defaultPassword));
                System.out.println("Default password: " + defaultPassword);
            }

            // Lưu user đã được cập nhật vào cơ sở dữ liệu
            return userRepo.save(user);
        } else {
            throw new DataNotFoundException("User not found with the provided email: " + request.getEmail());
        }
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).get();
    }
    public User getUserById(String id){
        return userRepo.findById(id).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng với id: " + id));
    }


    public Page<User> getAllByRole(String role, Boolean isActive, int size, int page){
        Pageable pageable = PageRequest.of(page, size);
        RoleEnums roleEnum = null;
        if (role != null) {
            try {
                roleEnum = RoleEnums.valueOf(role);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Role không tồn tại: " + role, e);
            }
        }
        return userRepo.getUserByRole(roleEnum, isActive, pageable);

    }
}

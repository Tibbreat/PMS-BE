package sep490.g13.pms_be.service.entity;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.model.request.user.AddUserRequest;
import sep490.g13.pms_be.model.request.user.UpdateUserNameAndPasswordRequest;
import sep490.g13.pms_be.model.response.user.GetUsersOptionResponse;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.utils.CloudinaryService;
import sep490.g13.pms_be.utils.StringUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private CloudinaryService cloudinaryService;

    public User addUser(AddUserRequest request, MultipartFile image) {

        String accountName = StringUtils.generateUsername(request.getFullName());
        int count = userRepo.countByUsernameContaining(accountName);
        String username = count == 0 ? accountName : accountName + (count + 1);

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setUsername(username.trim());
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode("pms@" + request.getIdCardNumber()));
        user.setEmail(username.trim() + "@pms.com");

        if (image != null && !image.isEmpty()) {
            String imagePath = cloudinaryService.saveImage(image);
            user.setImageLink(imagePath);
        }

        return userRepo.save(user);
    }


    public User updateUserNameAndPassword(UpdateUserNameAndPasswordRequest request) {
        Optional<User> existingUserOpt = userRepo.findByEmail(request.getEmail());
        if (existingUserOpt.isPresent()) {

            User user = existingUserOpt.get();
            if (user.getUsername() != null) {
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

    public User getUserById(String id) {
        return userRepo.findById(id).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng với id: " + id));
    }

    public Page<User> getAllByRole(List<String> roles, Boolean isActive, String schoolId, int size, int page) {
        Pageable pageable = PageRequest.of(page, size);
        List<RoleEnums> roleEnums = new ArrayList<>();

        if (roles != null && !roles.isEmpty()) {
            for (String role : roles) {
                try {
                    RoleEnums roleEnum = RoleEnums.valueOf(role);
                    roleEnums.add(roleEnum);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Role không tồn tại: " + role, e);
                }
            }
        }

        return userRepo.getUsersByRoles(roleEnums, isActive, schoolId, pageable);
    }


    public void changeUserStatus(String userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng với id: " + userId));
        Boolean newStatus = !user.getIsActive();
        int updatedRows = userRepo.updateUserStatus(userId, newStatus);
        if (updatedRows == 0) {
            throw new DataNotFoundException("Không tìm thấy người dùng với id: " + userId);
        }
    }

    public List<GetUsersOptionResponse> getUsers(String role) {
        try {
            RoleEnums roleEnum = RoleEnums.valueOf(role);
            return userRepo.findAllByRole(roleEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role không tồn tại: " + role, e);
        }
    }

    public List<GetUsersOptionResponse> getUserswithUserName(String role) {
        try {
            RoleEnums roleEnum = RoleEnums.valueOf(role);
            return userRepo.findAllByRoleWithUserName(roleEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role không tồn tại: " + role, e);
        }
    }
}

package sep490.g13.pms_be.service.entity;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.model.request.user.AddUserRequest;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.StringUtils;

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

        System.out.println("Default password: " + defaultPassword);
        System.out.println("Username: " + username);

        return userRepo.save(user);
    }
    public User getUserById(String id){
        return userRepo.findById(id).orElse(null);
    }
}

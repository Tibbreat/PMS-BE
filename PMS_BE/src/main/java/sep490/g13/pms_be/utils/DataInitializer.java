package sep490.g13.pms_be.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.enums.RoleEnums;

@Component
public class DataInitializer {

    @Autowired
    private UserRepo userRepo;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if(userRepo.count() == 0) {
            userRepo.save(User.builder()
                    .role(RoleEnums.TEACHER)
                    .isActive(true)
                    .email("anhnp@pms.com")
                    .fullName("Nguyễn Phương Anh")
                    .address("Hà Nội")
                    .phone("0823193169")
                    .username("AnhNP")
                    .password(passwordEncoder.encode("12345678"))
                    .build());
            userRepo.save(User.builder()
                    .role(RoleEnums.TEACHER)
                    .email("hoanglt@pms.com")
                    .isActive(true)
                    .fullName("Lê Thị Hoàng")
                    .address("Đà Nẵng")
                    .phone("0987654321")
                    .username("HoangLT")
                    .password(passwordEncoder.encode("12345678"))
                    .build());
            userRepo.save(User.builder()
                    .role(RoleEnums.TEACHER)
                    .isActive(false)
                    .email("huongnt@pms.com")
                    .fullName("Nguyễn Thị Hương")
                    .address("Vinh")
                    .phone("0812345678")
                    .username("HuongNT")
                    .password(passwordEncoder.encode("12345678"))
                    .build());
            userRepo.save(User.builder()
                    .role(RoleEnums.TEACHER)
                    .isActive(false)
                    .email("namnd@pms.com")
                    .fullName("Nguyễn Đình Nam")
                    .address("Huế")
                    .phone("0921987654")
                    .username("NamND")
                    .password(passwordEncoder.encode("12345678"))
                    .build());
            userRepo.save(User.builder()
                    .role(RoleEnums.ADMIN)
                    .isActive(true)
                    .email("kiennt@pms.com")
                    .fullName("Nguyễn Trung Kiên")
                    .address("Thanh Hóa")
                    .phone("0943494158")
                    .username("KienNT")
                    .password(passwordEncoder.encode("12345678"))
                    .build());
        }
    }
}

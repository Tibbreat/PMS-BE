package sep490.g13.pms_be.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.ClassTeacherRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer {

    @Autowired
    private UserRepo userRepo;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    private ClassTeacherRepo classTeacherRepo;

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
            userRepo.save(User.builder()
                    .role(RoleEnums.CLASS_MANAGER)
                    .isActive(true)
                    .email("minhtuan@pms.com")
                    .fullName("Nguyễn Minh Tuấn")
                    .address("Hà Nội")
                    .phone("0812334455")
                    .username("TuanNM")
                    .password(passwordEncoder.encode("12345678"))
                    .build());

            userRepo.save(User.builder()
                    .role(RoleEnums.CLASS_MANAGER)
                    .isActive(true)
                    .email("hongngoc@pms.com")
                    .fullName("Phạm Hồng Ngọc")
                    .address("Hồ Chí Minh")
                    .phone("0821445566")
                    .username("NgocPH")
                    .password(passwordEncoder.encode("12345678"))
                    .build());

            userRepo.save(User.builder()
                    .role(RoleEnums.CLASS_MANAGER)
                    .isActive(true)
                    .email("vanlong@pms.com")
                    .fullName("Lê Văn Long")
                    .address("Đà Nẵng")
                    .phone("0831556677")
                    .username("LongLV")
                    .password(passwordEncoder.encode("12345678"))
                    .build());

            userRepo.save(User.builder()
                    .role(RoleEnums.CLASS_MANAGER)
                    .isActive(true)
                    .email("thuylinh@pms.com")
                    .fullName("Trần Thùy Linh")
                    .address("Hải Phòng")
                    .phone("0841667788")
                    .username("LinhTT")
                    .password(passwordEncoder.encode("12345678"))
                    .build());

            userRepo.save(User.builder()
                    .role(RoleEnums.CLASS_MANAGER)
                    .isActive(true)
                    .email("minhhoang@pms.com")
                    .fullName("Vũ Minh Hoàng")
                    .address("Cần Thơ")
                    .phone("0851778899")
                    .username("HoangVM")
                    .password(passwordEncoder.encode("12345678"))
                    .build());

            userRepo.save(User.builder()
                    .role(RoleEnums.CLASS_MANAGER)
                    .isActive(true)
                    .email("thanhbinh@pms.com")
                    .fullName("Nguyễn Thanh Bình")
                    .address("Nha Trang")
                    .phone("0861889900")
                    .username("BinhNT")
                    .password(passwordEncoder.encode("12345678"))
                    .build());

            userRepo.save(User.builder()
                    .role(RoleEnums.CLASS_MANAGER)
                    .isActive(true)
                    .email("hoanglam@pms.com")
                    .fullName("Hoàng Đức Lâm")
                    .address("Bình Dương")
                    .phone("0871990011")
                    .username("LamHD")
                    .password(passwordEncoder.encode("12345678"))
                    .build());

            userRepo.save(User.builder()
                    .role(RoleEnums.CLASS_MANAGER)
                    .isActive(true)
                    .email("ngocanh@pms.com")
                    .fullName("Lê Ngọc Anh")
                    .address("Vũng Tàu")
                    .phone("0881223344")
                    .username("AnhLN")
                    .password(passwordEncoder.encode("12345678"))
                    .build());

            userRepo.save(User.builder()
                    .role(RoleEnums.CLASS_MANAGER)
                    .isActive(true)
                    .email("khanhlinh@pms.com")
                    .fullName("Đỗ Khánh Linh")
                    .address("Huế")
                    .phone("0891334455")
                    .username("LinhDK")
                    .password(passwordEncoder.encode("12345678"))
                    .build());
        }


    }
}

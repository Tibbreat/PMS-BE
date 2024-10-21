package sep490.g13.pms_be.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sep490.g13.pms_be.entities.*;
import sep490.g13.pms_be.repository.*;
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
    private FoodServiceProviderRepo foodServiceProviderRepo;

    @Autowired
    private TransportServiceProviderRepo transportServiceProviderRepo;


    @PostConstruct
    public void init() {
        if (userRepo.count() == 0) {
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
        }

        if (foodServiceProviderRepo.count() == 0) {
            foodServiceProviderRepo.save(FoodServiceProvider.builder()
                    .providerName("Công ty TNHH Thực phẩm Hà Nội")
                    .providerTaxCode("0108765432")
                    .providerPhone("02434567890")
                    .providerEmail("contact@thucphamhn.com")
                    .providerAddress("Số 20, Phố Láng Hạ, Quận Ba Đình, Hà Nội")
                    .representativeName("Nguyễn Thị B")
                    .representativePosition("Giám đốc")
                    .bankName("VCB")
                    .isActive(Boolean.TRUE)
                    .bankAccountNumber("00234567890")
                    .build());
        }
        if (transportServiceProviderRepo.count() == 0) {
            transportServiceProviderRepo.save(TransportServiceProvider.builder()
                    .providerName("Công ty TNHH Dịch vụ Đưa đón Hà Nội")
                    .providerTaxCode("0108765432")
                    .providerPhone("02434567890")
                    .providerEmail("contact@dichvuduadonhn.com")
                    .providerAddress("Số 20, Phố Láng Hạ, Quận Ba Đình, Hà Nội")
                    .representativeName("Nguyễn Văn C")
                    .representativePosition("Giám đốc")
                    .bankName("VCB")  // Vietcombank
                    .bankAccountNumber("00234567890")
                    .isActive(Boolean.TRUE)
                    .totalVehicle(10)
                    .build());
        }

    }
}

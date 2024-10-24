package sep490.g13.pms_be.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sep490.g13.pms_be.entities.*;
import sep490.g13.pms_be.repository.*;
import sep490.g13.pms_be.utils.enums.RoleEnums;

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

    @Autowired
    private SchoolRepo schoolRepo;

    @PostConstruct
    public void init() {
        initializeSchool();
        initializeAdminUser();
        initializeFoodServiceProvider();
        initializeTransportServiceProvider();
    }

    private void initializeSchool() {
        if (schoolRepo.count() == 0) {
            School school = School.builder()
                    .schoolName("Trường mầm non Thạch Hòa")
                    .schoolAddress("Xã Thạch Hoà, Huyện Thạch Thất, Hà Nội")
                    .phoneContact("0978 056 529")
                    .emailContact("mnthachhoa@edu.vn")
                    .build();
            schoolRepo.save(school);
        }
    }

    private void initializeAdminUser() {
        if (userRepo.count() == 0) {


                userRepo.save(User.builder()
                        .role(RoleEnums.ADMIN)
                        .isActive(true)
                        .email("admin@pms.com")
                        .phone("0943494158")
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .build());
            
        }
    }

    private void initializeFoodServiceProvider() {
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
                    .isActive(true)
                    .beneficiaryName("Nguyễn Thị B")
                    .bankAccountNumber("00234567890")
                    .build());
        }
    }

    private void initializeTransportServiceProvider() {
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
                    .beneficiaryName("Nguyễn Văn C")
                    .bankAccountNumber("00234567890")
                    .isActive(true)
                    .totalVehicle(10)
                    .build());
        }
    }
}

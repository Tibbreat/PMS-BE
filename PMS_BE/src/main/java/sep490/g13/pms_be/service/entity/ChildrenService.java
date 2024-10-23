package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.*;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.model.request.children.AddChildrenRequest;
import sep490.g13.pms_be.model.response.children.ChildrenDetailResponse;
import sep490.g13.pms_be.model.response.children.ChildrenListResponse;
import sep490.g13.pms_be.repository.*;
import sep490.g13.pms_be.service.utils.CloudinaryService;
import sep490.g13.pms_be.utils.StringUtils;
import sep490.g13.pms_be.utils.enums.RoleEnums;

@Service
public class ChildrenService {

    @Autowired
    private ChildrenRepo childrenRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RelationshipRepo relationshipRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private VehicleRepo vehicleRepo;
    @Autowired
    private ChildrenVehicleRegistrationRepo childrenVehicleRegistrationRepo;

    @Transactional
    public Children addChildren(AddChildrenRequest request, MultipartFile image) {
        Children children = Children.builder()
                .childName(request.getChildName())
                .childBirthDate(request.getChildBirthDate())
                .childAddress(request.getChildAddress())
                .birthAddress(request.getBirthAddress())
                .gender(request.getGender())
                .imageUrl(cloudinaryService.saveImage(image))
                .religion(request.getReligion())
                .nationality(request.getNationality())
                .isRegisteredForBoarding(Boolean.FALSE)
                .isRegisteredForTransport(Boolean.FALSE)
                .build();
        children.setCreatedBy(request.getCreatedBy());

        Children newChildren = childrenRepo.save(children);

        User fatherExist = userRepo.existByIdCardNumber(request.getFather().getIdCardNumber());
        User motherExist = userRepo.existByIdCardNumber(request.getMother().getIdCardNumber());

        if (fatherExist != null || motherExist != null) {
            if (fatherExist != null) {
                relationshipRepo.save(Relationship.builder()
                        .childrenId(children)
                        .parentId(fatherExist)
                        .relationship("Father")  // Set the relationship as Father
                        .build());
            }

            if (motherExist != null) {
                relationshipRepo.save(Relationship.builder()
                        .childrenId(children)
                        .parentId(motherExist)
                        .relationship("Mother")  // Set the relationship as Mother
                        .build());
            }
        } else {
            // For Father
            User father = userRepo.save(User.builder()
                    .username("parent." + StringUtils.generateUsername(request.getChildName()).toLowerCase())
                    .password(passwordEncoder.encode("123456"))
                    .fullName(request.getFather().getFullName())
                    .idCardNumber(request.getFather().getIdCardNumber())
                    .phone(request.getFather().getPhone())
                    .isActive(Boolean.TRUE)
                    .role(RoleEnums.PARENT).build());

            relationshipRepo.save(Relationship.builder()
                    .childrenId(children)
                    .parentId(father)
                    .relationship("Father")  // Explicitly set the relationship as Father
                    .build());

            // For Mother
            User mother = userRepo.save(User.builder()
                    .fullName(request.getMother().getFullName())
                    .idCardNumber(request.getMother().getIdCardNumber())
                    .phone(request.getMother().getPhone())
                    .isActive(Boolean.TRUE)
                    .role(RoleEnums.PARENT).build());

            relationshipRepo.save(Relationship.builder()
                    .childrenId(children)
                    .parentId(mother)
                    .relationship("Mother")  // Explicitly set the relationship as Mother
                    .build());
        }

        return newChildren;
    }

    public Page<ChildrenListResponse> findChildrenByFilter(String classId, String childName, int page, int size) {
        return childrenRepo.findChildrenByFilter(classId, childName, PageRequest.of(page, size));
    }

    public ChildrenDetailResponse getChildrenDetail(String childrenId) {
        return childrenRepo.findChildrenDetailById(childrenId);
    }

    @Transactional
    public void updateServiceStatus(String childrenId, String service, String vehicleId) {
        Children children = childrenRepo.findById(childrenId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy dữ liệu của trẻ"));
        switch (service) {
            case "transport":
                if (children.getIsRegisteredForTransport().equals(Boolean.TRUE)) {
                    childrenVehicleRegistrationRepo.deleteByChildrenId(childrenId);
                    childrenRepo.updateTransportServiceStatus(childrenId, Boolean.FALSE);
                } else {
                    Vehicle vehicle = vehicleRepo.findById(vehicleId)
                            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy phương tiện tương ứng"));

                    //Check if the vehicle is already full
                    if (childrenVehicleRegistrationRepo.countByVehicleId(vehicleId) >= vehicle.getNumberOfSeats()) {
                        throw new IllegalArgumentException("Xe hiện tại không còn chỗ");
                    }
                    childrenRepo.updateTransportServiceStatus(childrenId, Boolean.TRUE);
                    childrenVehicleRegistrationRepo.save(ChildrenVehicleRegistration.builder()
                            .children(children)
                            .vehicle(vehicle)
                            .build());
                }
                break;
            case "boarding":
                childrenRepo.updateBoardingServiceStatus(childrenId, !children.getIsRegisteredForBoarding());
                break;
            default:
                throw new IllegalArgumentException("Invalid service type: " + service);
        }
    }
}

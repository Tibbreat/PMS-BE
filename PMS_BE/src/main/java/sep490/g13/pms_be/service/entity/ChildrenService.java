package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.Relationship;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.model.request.children.AddChildrenRequest;
import sep490.g13.pms_be.model.response.children.ChildrenDetailResponse;
import sep490.g13.pms_be.model.response.children.ChildrenListResponse;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.repository.RelationshipRepo;
import sep490.g13.pms_be.repository.UserRepo;
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
            relationshipRepo.save(Relationship.builder()
                    .childrenId(children)
                    .parentId(fatherExist)
                    .relationship("Father")
                    .build());
            relationshipRepo.save(Relationship.builder()
                    .childrenId(children)
                    .parentId(motherExist)
                    .relationship("Mother")
                    .build());
        } else {
            relationshipRepo.save(Relationship.builder()
                    .childrenId(children)
                    .parentId(userRepo.save(User.builder()
                            .username("parent." + StringUtils.generateUsername(request.getChildName()).toLowerCase())
                            .password(passwordEncoder.encode("123456"))
                            .fullName(request.getFather().getFullName())
                            .idCardNumber(request.getFather().getIdCardNumber())
                            .phone(request.getFather().getPhone())
                            .isActive(Boolean.TRUE)
                            .role(RoleEnums.PARENT).build()))
                    .build());
            relationshipRepo.save(Relationship.builder()
                    .childrenId(children)
                    .parentId(userRepo.save(User.builder()
                            .fullName(request.getMother().getFullName())
                            .idCardNumber(request.getMother().getIdCardNumber())
                            .phone(request.getMother().getPhone())
                            .isActive(Boolean.TRUE)
                            .role(RoleEnums.PARENT).build()))
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
    public void updateServiceStatus(String childrenId, String service) {
        Children children = childrenRepo.findById(childrenId)
                .orElseThrow(() -> new RuntimeException("Data not found"));

        switch (service) {
            case "transport" ->
                    childrenRepo.updateTransportServiceStatus(childrenId, !children.getIsRegisteredForTransport());
            case "boarding" ->
                    childrenRepo.updateBoardingServiceStatus(childrenId, !children.getIsRegisteredForBoarding());
            default -> throw new IllegalArgumentException("Invalid service type: " + service);
        }
    }

}

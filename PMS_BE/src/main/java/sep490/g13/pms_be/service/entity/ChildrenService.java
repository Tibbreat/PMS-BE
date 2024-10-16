package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.Relationship;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.RelationshipRequest;
import sep490.g13.pms_be.model.request.children.UpdateChildrenRequest;
import sep490.g13.pms_be.model.request.user.AddUserRequest;
import sep490.g13.pms_be.model.response.CloudinaryResponse;
import sep490.g13.pms_be.model.response.children.ChildrenDetailResponse;
import sep490.g13.pms_be.model.response.children.ChildrenListResponse;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.RelationshipRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.utils.CloudinaryService;
import sep490.g13.pms_be.utils.FileUploadUtil;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChildrenService {
    @Autowired
    private ChildrenRepo childrenRepo;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    private RelationshipRepo relationshipRepo;

    public List<Children> findAllById(List<String> childrenId) {
        List<Children> childrenList = childrenRepo.findAllById(childrenId);
        if (childrenId.size() != childrenList.size()) {
            throw new IllegalArgumentException("Some Children were not found in the database");
        }
        return childrenList;
    }

    public Set<Children> findChildrenWithClasses(List<String> childrenIds) {
        return childrenRepo.findAllById(childrenIds).stream()
                .filter(children -> children.getSchoolClass() != null)
                .collect(Collectors.toSet());
    }

    public Children addChildren(Children c, List<RelationshipRequest> relationshipRequests, AddUserRequest addUserRequest1, AddUserRequest addUserRequest2) {

        // Kiểm tra xem createdBy có null hay không
        if (c.getCreatedBy() == null) {
            throw new IllegalArgumentException("CreatedBy field is null or invalid");
        }

        // Tìm user dựa trên createdBy ID
        User createdByUser = userRepo.findById(c.getCreatedBy())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + c.getCreatedBy()));

        // Thiết lập createdBy cho đối tượng Children
        c.setCreatedBy(createdByUser.getId());

        // Chỉ cho phép ADMIN tạo học sinh
        if (createdByUser.getRole() != RoleEnums.ADMIN && createdByUser.getRole() != RoleEnums.CLASS_MANAGER) {
            throw new PermissionNotAcceptException("Just Admin and Class_Manager can create children");
        }

        // Xử lý relationship nếu có
        Set<Relationship> relationships = new HashSet<>();
        if (relationshipRequests != null && !relationshipRequests.isEmpty()) {
            for (RelationshipRequest request : relationshipRequests) {

                // Tạo mới Relationship
                Relationship relationship = new Relationship();

                relationship.setChildrenId(c); // Gán Children cho Relationship
                relationship.setRelationship(request.getRelationship());
                relationship.setIsRepresentative(request.getIsRepresentative());
                if (request.getIsRepresentative() == true) {
                    User parent = userService.addUser(addUserRequest1, null);
                    relationship.setParentId(parent);
                } else {
                    User parentNo = User.
                            builder().
                            role(addUserRequest2.getRole()).
                            fullName(addUserRequest2.getFullName()).
                            idCardNumber(addUserRequest2.getIdCardNumber()).
                            phone(addUserRequest2.getPhone()).
                            address(addUserRequest2.getAddress()).
                            build();


                    userRepo.save(parentNo);
                    relationship.setParentId(parentNo);
                }
                // Thêm từng Relationship vào danh sách của Children
                relationships.add(relationship); // Thêm vào danh sách
            }
        }

        // Gán danh sách relationships cho Children
        c.setRelationships(relationships); // Giả sử bạn đã thêm trường relationships vào Children

        // Lưu đối tượng Children vào database
        return childrenRepo.save(c);
    }


    @Transactional
    public void uploadImage(final String id, final MultipartFile file) {
        final Children children = this.childrenRepo.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Children not found"));
        FileUploadUtil.assertAllowedExtension(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResponse response = this.cloudinaryService.uploadFile(file, fileName);
        children.setImageUrl(response.getUrl());
        children.setCloudinaryImageId(response.getPublicId());
        this.childrenRepo.save(children);
    }

    public Children updateChildren(Children child) {
        return childrenRepo.save(child); // Lưu lại đối tượng Children đã được cập nhật
    }

    public Page<Children> getChildrenByFilters(String fullname, String classId, int page, int size) {
        // Tạo Pageable để phân trang
        Pageable pageable = PageRequest.of(page, size);
        // Gọi repository để lấy danh sách lớp học theo bộ lọc
        return childrenRepo.findChildrenByFilter(classId, fullname, pageable);
    }


    public ChildrenDetailResponse getChildrenDetailById(String childId) {
        // Tìm đứa trẻ theo ID
        Optional<Children> childOptional = childrenRepo.findById(childId);

        if (childOptional.isPresent()) {
            Children child = childOptional.get();

            // Lấy danh sách Relationship của trẻ từ database
            List<Relationship> relationships = relationshipRepo.findByChildrenId(child);

            // Chuyển đổi List<Relationship> thành List<RelationshipRequest>
            List<RelationshipRequest> relationshipRequests = relationships.stream()
                    .map(relationship -> {
                        // Lấy thông tin của cha mẹ từ relationship
                        User parent = relationship.getParentId(); // Giả sử phương thức này trả về User

                        // Chuyển đổi từ User thành AddUserRequest
                        AddUserRequest parentRequest = AddUserRequest.builder()
                                .fullName(parent.getFullName())
                                .idCardNumber(parent.getIdCardNumber())
                                .address(parent.getAddress())
                                .phone(parent.getPhone())
                                .role(parent.getRole())
                                .build();

                        // Tạo đối tượng RelationshipRequest
                        return RelationshipRequest.builder()
                                .parent(parentRequest) // Sử dụng AddUserRequest thay vì User
                                .relationship(relationship.getRelationship())
                                .isRepresentative(relationship.getIsRepresentative())
                                .build();
                    })
                    .collect(Collectors.toList());

            // Tạo và trả về ChildrenDetailResponse
            return ChildrenDetailResponse.builder()
                    .childName(child.getChildName())
                    .childAge(child.getChildAge())
                    .childBirthDate(child.getChildBirthDate())
                    .childAddress(child.getChildAddress())
                    .isRegisterForTransport(child.getIsRegisteredForTransport())
                    .isRegisterForBoarding(child.getIsRegisteredForBoarding())
                    .imageUrl(child.getImageUrl())
                    .relationships(relationshipRequests) // Danh sách mối quan hệ đã được chuyển đổi
                    .classId(child.getSchoolClass() != null ? child.getSchoolClass().getId() : null)
                    .identificationNumber(child.getIdentificationNumber())
                    .nationality(child.getNationality())
                    .people(child.getPeople())
                    .birthAddress(child.getBirthAddress())
                    .build();
        } else {
            throw new RuntimeException("Children not found with ID: " + childId);
        }
    }


    @Transactional
    public void updateTransportRegistration(String childId, Boolean isRegisteredForTransport) {
        Optional<Children> childOpt = childrenRepo.findById(childId);
        if (childOpt.isPresent()) {
            Children child = childOpt.get();
            child.setIsRegisteredForTransport(isRegisteredForTransport);
            childrenRepo.save(child);
        } else {
            throw new RuntimeException("Child not found with id: " + childId);
        }
    }

    @Transactional
    public void updateBoardingRegistration(String childId, Boolean isRegisteredForBoarding) {
        Optional<Children> childOpt = childrenRepo.findById(childId);
        if (childOpt.isPresent()) {
            Children child = childOpt.get();
            child.setIsRegisteredForBoarding(isRegisteredForBoarding);
            childrenRepo.save(child);
        } else {
            throw new RuntimeException("Child not found with id: " + childId);
        }
    }

    @Transactional
    public Children updateChildrenInformation(String childId, UpdateChildrenRequest updateChildrenRequest) {

        Children existingChild = childrenRepo.findById(childId)
                .orElseThrow(() -> new DataNotFoundException("Child not found with id: " + childId));
        existingChild.setChildName(updateChildrenRequest.getChildName());
        existingChild.setChildAge(updateChildrenRequest.getChildAge());
        existingChild.setChildBirthDate(updateChildrenRequest.getChildBirthDate());
        existingChild.setChildAddress(updateChildrenRequest.getChildAddress());
        User lastModifiedBy = userRepo.findById(updateChildrenRequest.getLastModifiedById())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + updateChildrenRequest.getLastModifiedById()));
        if (!lastModifiedBy.getRole().equals(RoleEnums.ADMIN)) {
            throw new PermissionNotAcceptException("Only ADMIN can modify this child.");
        }
        existingChild.setLastModifiedBy(updateChildrenRequest.getLastModifiedById());
        // Lưu lại đối tượng Children đã được cập nhật
        return childrenRepo.save(existingChild);
    }

    public Page<ChildrenListResponse> getChildrenByClass(String classId, int size, int page) {
        Pageable pageable = PageRequest.of(page, size);
        return childrenRepo.findAllBySchoolClassId(classId, pageable);
    }

    public List<ChildrenListResponse> getChildrenByClassId(String classId) {
        return childrenRepo.findAllByClassId(classId);
    }
    @Transactional
    public void updateChildrenClass(String childId, String classId) {
        Children existingClazz = childrenRepo.findById(classId)
                .orElseThrow(() -> new DataNotFoundException("Class not found with id: " + classId));

        Children existingChild = childrenRepo.findById(childId)
                .orElseThrow(() -> new DataNotFoundException("Child not found with id: " + childId));

        childrenRepo.updateChildrenClass(childId, classId);
    }


}

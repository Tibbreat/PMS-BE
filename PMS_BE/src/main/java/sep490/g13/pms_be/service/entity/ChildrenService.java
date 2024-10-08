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
import sep490.g13.pms_be.model.request.user.UpdateUserNameAndPasswordRequest;
import sep490.g13.pms_be.model.response.CloudinaryResponse;
import sep490.g13.pms_be.model.response.children.ChildrenDetailResponse;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.utils.CloudinaryService;
import sep490.g13.pms_be.utils.FileUploadUtil;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.*;
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

    public List<Children> findAllById(List<String> childrenId) {
        // Tìm tất cả các SkillTag theo danh sách ID được cung cấp
        List<Children> childrenList = childrenRepo.findAllById(childrenId);

        // Kiểm tra nếu có bất kỳ SkillTag nào không tồn tại trong cơ sở dữ liệu
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

    public Children addChildren(Children c, List<RelationshipRequest> relationshipRequests) {

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
                // Tìm parent dựa trên parentId
                User parent = userRepo.findById(request.getParentId())
                        .orElseThrow(() -> new DataNotFoundException("Parent not found with id: " + request.getParentId()));


                // Tạo mới Relationship
                Relationship relationship = new Relationship();
                relationship.setParentId(parent);
                relationship.setChildrenId(c);
                relationship.setRelationship(request.getRelationship());
                relationship.setIsRepresentative(request.getIsRepresentative());

                // Thêm từng Relationship vào danh sách của Children
                if (request.getIsRepresentative()) {
                    // Chuẩn bị đối tượng cập nhật
                    UpdateUserNameAndPasswordRequest updateUserRequest = new UpdateUserNameAndPasswordRequest();
                    updateUserRequest.setFullName(parent.getFullName());
                    updateUserRequest.setUserName(parent.getUsername());
                    updateUserRequest.setEmail(parent.getEmail());


                    // Cập nhật thông tin người dùng
                    User updatedUser = userService.updateUserNameAndPassword(updateUserRequest);
                    relationships.add(relationship);
                    System.out.println("User updated with new password: " + updatedUser.getPassword());
                }
            }
        }
        System.out.println(relationships.size());
        c.setRelationships(relationships);

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

    // Trong ChildrenService.java
    public ChildrenDetailResponse getChildrenDetailById(String childId) {
        // Tìm đứa trẻ theo ID
        Optional<Children> childOptional = childrenRepo.findById(childId);

        if (childOptional.isPresent()) {
            Children child = childOptional.get();

            // Tạo và trả về ChildrenDetailResponse
            return ChildrenDetailResponse.builder()
                    .childName(child.getChildName())
                    .childAge(child.getChildAge())
                    .childBirthDate(child.getChildBirthDate())
                    .childAddress(child.getChildAddress())
                    .classId(child.getSchoolClass().getId()) // Lấy ID của lớp mà trẻ sẽ được thêm vào
                    .relationships(child.getRelationships().stream()
                            .map(relationship -> new RelationshipRequest(
                                    relationship.getParentId().getId(),
                                    relationship.getRelationship(),
                                    relationship.getIsRepresentative()))
                            .collect(Collectors.toList())) // Chuyển đổi Set sang List
                    .imageUrl(child.getImageUrl()) // Lấy URL hình ảnh
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

        Classes schoolClass = classRepo.findById(updateChildrenRequest.getClassId())
                .orElseThrow(() -> new DataNotFoundException("Class not found with id: " + updateChildrenRequest.getClassId()));
        existingChild.setSchoolClass(schoolClass);


        User lastModifiedBy = userRepo.findById(updateChildrenRequest.getLastModifiedById())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + updateChildrenRequest.getLastModifiedById()));
        if (!lastModifiedBy.getRole().equals(RoleEnums.ADMIN)) {
            throw new PermissionNotAcceptException("Only ADMIN can modify this child.");
        }

        existingChild.setLastModifiedBy(updateChildrenRequest.getLastModifiedById());

        if (updateChildrenRequest.getRelationships() != null) {
            // Xóa các mối quan hệ hiện tại và đặt các mối quan hệ mới
            existingChild.getRelationships().clear();
            updateChildrenRequest.getRelationships().forEach(relationshipRequest -> {
                Relationship relationship = new Relationship();
                User parent = userRepo.findById(relationshipRequest.getParentId())
                        .orElseThrow(() -> new DataNotFoundException("Parent not found with id: " + relationshipRequest.getParentId()));

                relationship.setChildrenId(existingChild);
                relationship.setParentId(parent);
                relationship.setRelationship(relationshipRequest.getRelationship());
                relationship.setIsRepresentative(relationshipRequest.getIsRepresentative());

                existingChild.getRelationships().add(relationship);
            });
        }
        // Lưu lại đối tượng Children đã được cập nhật
        return childrenRepo.save(existingChild);
    }




}

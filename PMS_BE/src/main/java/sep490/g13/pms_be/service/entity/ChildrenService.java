package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.Relationship;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.RelationshipRequest;
import sep490.g13.pms_be.model.response.CloudinaryResponse;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.CloudinaryService;
import sep490.g13.pms_be.utils.FileUploadUtil;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChildrenService {
    @Autowired
    private ChildrenRepo childrenRepo;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserRepo userRepo;

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
        if (createdByUser.getRole() != RoleEnums.ADMIN && createdByUser.getRole() != RoleEnums.Class_Manager) {
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
                relationships.add(relationship);
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


}

package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.response.CloudinaryResponse;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.CloudinaryService;
import sep490.g13.pms_be.utils.FileUploadUtil;
import sep490.g13.pms_be.utils.enums.RoleEnums;

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
    public Children addChildren(Children c){

        if (c.getCreatedBy()== null) {
            throw new IllegalArgumentException("CreatedBy field is null or invalid");
        }

//         Fetch the user from the database based on the createdBy ID
        User createdByUser = userRepo.findById(c.getCreatedBy())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + c.getCreatedBy()));

        // Set the createdBy field in the class entity
        c.setCreatedBy(createdByUser.getId());

        // Ensure that only INTERVIEWER or ADMIN role can create a class
        if (createdByUser.getRole() != RoleEnums.ADMIN) {
            throw new PermissionNotAcceptException("Cant create class with other role");
        }

        return childrenRepo.save(c);
    }
    @Transactional
    public void uploadImage(final String id, final MultipartFile file) {
        final Children children = this.childrenRepo.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        FileUploadUtil.assertAllowedExtension(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResponse response = this.cloudinaryService.uploadFile(file, fileName);
        children.setImageUrl(response.getUrl());
        children.setCloudinaryImageId(response.getPublicId());
        this.childrenRepo.save(children);
    }

}

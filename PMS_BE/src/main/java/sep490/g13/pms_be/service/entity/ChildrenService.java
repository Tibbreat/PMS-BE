package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.repository.ChildrenRepo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChildrenService {
    @Autowired
    private ChildrenRepo childrenRepo;

    public List<Children> findAllById(List<String> childrenId) {
        // Tìm tất cả các SkillTag theo danh sách ID được cung cấp
        List<Children> childrenList = childrenRepo.findAllById(childrenId);

        // Kiểm tra nếu có bất kỳ SkillTag nào không tồn tại trong cơ sở dữ liệu
        if (childrenId.size() != childrenId.size()) {
            throw new IllegalArgumentException("Some Children were not found in the database");
        }

        return childrenList;
    }
    public Set<Children> findChildrenWithClasses(List<String> childrenIds) {
        return childrenRepo.findAllById(childrenIds).stream()
                .filter(children -> children.getSchoolClass() != null)
                .collect(Collectors.toSet());
    }

}

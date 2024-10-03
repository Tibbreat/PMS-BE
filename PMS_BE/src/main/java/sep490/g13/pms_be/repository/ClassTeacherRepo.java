package sep490.g13.pms_be.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.User;

@Repository
public interface ClassTeacherRepo extends CrudRepository<ClassTeacher, Integer> {
    User getById(String id);
}

package sep490.g13.pms_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.User;

import java.util.List;

@Repository
public interface TeacherRepo extends JpaRepository<User,String> {
    List<User> findAllByIdIn(List<Long> teacherIds);
}

package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.ClassTeacher;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;


@Repository
public interface ClassTeacherRepo extends CrudRepository<ClassTeacher, Integer> {
    User getById(String id);

    @Query("SELECT ct.schoolClasses FROM ClassTeacher ct WHERE ct.teacherId.id = :teacherId")
    Page<Classes> findClassesByTeacherId(String teacherId, Pageable page);
}


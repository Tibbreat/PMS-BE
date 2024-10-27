package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.model.request.classes.UpdateClassRequest;
import sep490.g13.pms_be.model.response.classes.ClassDetailResponse;
import sep490.g13.pms_be.model.response.classes.ClassListResponse;
import sep490.g13.pms_be.model.response.user.TeacherOfClassResponse;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassRepo extends JpaRepository<Classes, String> {
    @Query("SELECT new sep490.g13.pms_be.model.response.classes.ClassListResponse(c.id, c.className, c.ageRange, " +
            " c.openingDay, c.manager.id, c.manager.username, c.academicYear, c.status) " +
            "FROM Classes c " +
            "LEFT JOIN c.manager " +
            "WHERE (:schoolYear IS NULL OR YEAR(c.openingDay) = :schoolYear) " +
            "AND (:ageRange IS NULL OR c.ageRange = :ageRange) " +
            "AND (:managerId IS NULL OR c.manager.id = :managerId) " +
            "ORDER BY c.createdDate, c.ageRange, c.openingDay DESC")
    Page<ClassListResponse>  findClassesByFilters(
            @Param("schoolYear") Integer schoolYear,
            @Param("ageRange") String ageRange,
            @Param("managerId") String managerId,
            Pageable pageable);

    @Query("SELECT new sep490.g13.pms_be.model.response.user.TeacherOfClassResponse(teacher.teacherId.id, teacher.teacherId.username, teacher.teacherId.fullName) " +
            "FROM ClassTeacher teacher " +
            "WHERE teacher.schoolClasses.id = :id")
    List<TeacherOfClassResponse> getTeacherOfClass(String id);

    @Query("SELECT c FROM Classes c " +
            "WHERE (:managerId IS NULL OR c.manager.id = :managerId) " +
            "AND (:teacherId IS NULL OR EXISTS (SELECT ct FROM ClassTeacher ct WHERE ct.schoolClasses.id = c.id AND ct.teacherId.id = :teacherId))")
    List<Classes> findClassesByTeacherIdOrManagerId(String teacherId, String managerId);
}

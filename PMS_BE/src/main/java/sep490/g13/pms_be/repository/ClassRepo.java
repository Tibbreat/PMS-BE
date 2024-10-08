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
import sep490.g13.pms_be.model.request.classes.UpdateClassRequest;
import sep490.g13.pms_be.model.response.classes.ClassDetailResponse;
import sep490.g13.pms_be.model.response.classes.ClassListResponse;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassRepo extends JpaRepository<Classes, String> {
    @Query("SELECT new sep490.g13.pms_be.model.response.classes.ClassListResponse(c.id, c.className, c.ageRange, " +
            "c.closingDay, c.openingDay, c.manager.id, c.manager.username, c.status) " +
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

    @Modifying
    @Transactional
    @Query("UPDATE Classes c SET " +
            "c.openingDay = :#{#updateRequest.openingDay}, " +
            "c.closingDay = :#{#updateRequest.closingDay}, " +
            "c.manager.id = :#{#updateRequest.managerId}, " +
            "c.lastModifiedBy = :#{#updateRequest.lastModifyById} " +
            "WHERE c.id = :classId")
    void updateClass(@Param("classId") String classId, @Param("updateRequest") UpdateClassRequest updateRequest);

    @Transactional
    @Modifying
    @Query("UPDATE Classes j SET " +
            "j.status = :status " +
            "WHERE j.id = :classId")
    void updateClassesByStatus(@Param("status") String status,
                         @Param("classId")  String id);
    List<Classes> findByClosingDayBeforeAndStatus(LocalDate date, boolean status);
    List<Classes> findByOpeningDayAfterAndStatus(LocalDate date, boolean status);

    @Query("SELECT new sep490.g13.pms_be.model.response.classes.ClassDetailResponse(c.className, c.ageRange, c.openingDay, c.closingDay, " +
            "c.childrens, c.teachers) " +
            "FROM Classes c " +
            "JOIN Children children ON children.schoolClass.id = c.id " +
            "JOIN User teacher ON teacher.classTeachers." +
            "WHERE c.id = :id")
    ClassDetailResponse findClassById(String id);
}

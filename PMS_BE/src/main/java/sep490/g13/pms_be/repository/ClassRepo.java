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

@Repository
public interface ClassRepo extends JpaRepository<Classes, String> {
    @Query("SELECT c FROM Classes c " +
            "LEFT JOIN FETCH c.manager " +
            "WHERE (:schoolYear IS NULL OR YEAR(c.openingDay) = :schoolYear) " +
            "AND (:ageRange IS NULL OR c.ageRange = :ageRange) " +
            "AND (:managerId IS NULL OR c.manager.id = :managerId) " +
            "ORDER BY c.createdDate, c.ageRange, c.openingDay DESC")
    Page<Classes> findClassesByFilters(
            @Param("schoolYear") Integer schoolYear,
            @Param("ageRange") String ageRange,
            @Param("managerId") Long managerId,
            Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Classes c SET " +
            "c.openingDay = :#{#updateRequest.openingDay}, " +
            "c.closingDay = :#{#updateRequest.closingDay}, " +
            "c.manager.id = :#{#updateRequest.managerId}, " + // Chỉnh sửa tên trường
            "c.lastModifiedBy = :#{#updateRequest.lastModifyById} " + // Chỉnh sửa tên trường
            "WHERE c.id = :classId")
    void updateClass(@Param("classId") String classId, @Param("updateRequest") UpdateClassRequest updateRequest);



}

package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.Children;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface ChildrenRepo extends JpaRepository<Children, String> {

    @Query("SELECT ch FROM Children ch " +
            "LEFT JOIN FETCH ch.schoolClass sc " +
            "WHERE (:classId IS NULL OR sc.id = :classId) " +
            "AND (:childName IS NULL OR ch.childName LIKE CONCAT('%', :childName, '%')) " +
            "ORDER BY ch.childName")
    Page<Children> findChildrenByFilter(
            @Param("classId") String classId,
            @Param("childName") String childName,
            Pageable pageable);

}

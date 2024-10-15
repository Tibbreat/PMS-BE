package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.DailyMenu;

import java.time.LocalDate;

@Repository
public interface DailyMenuRepo extends JpaRepository<DailyMenu, String> {
    @Query("SELECT dm FROM DailyMenu dm " +
            "WHERE (:classId IS NULL OR dm.schoolClasses.id = :classId) " +
            "AND (:menuDate IS NULL OR dm.menuDate = :menuDate) " +
            "ORDER BY dm.menuDate DESC")
    Page<DailyMenu> findDailyMenuByFilters(
            @Param("classId") String classId,
            @Param("menuDate") LocalDate menuDate,
            Pageable pageable);
}

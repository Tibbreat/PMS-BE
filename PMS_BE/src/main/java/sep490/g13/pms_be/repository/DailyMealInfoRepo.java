package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.DailyMealInfo;

@Repository
public interface DailyMealInfoRepo extends JpaRepository<DailyMealInfo, String> {

    @Query("SELECT dmi FROM DailyMealInfo dmi " +
            "WHERE (:childId IS NULL OR dmi.child.id = :childId) " +
            "AND (:dailyMenuId IS NULL OR dmi.dailyMenu.id = :dailyMenuId) " +
            "ORDER BY dmi.createdDate DESC")
    Page<DailyMealInfo> findDailyMealInfoByChildIdAndMenuId(
            @Param("childId") String childId,
            @Param("dailyMenuId") String dailyMenuId,
            Pageable pageable);
}

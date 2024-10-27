package sep490.g13.pms_be.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.DailyMenu;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyMenuRepo extends JpaRepository<DailyMenu, String> {
    @Query("SELECT d FROM DailyMenu d WHERE d.date = :date")
    List<DailyMenu> findByDate(LocalDate date);

    List<DailyMenu> findByDateBetween(LocalDate startDate, LocalDate endDate);
}

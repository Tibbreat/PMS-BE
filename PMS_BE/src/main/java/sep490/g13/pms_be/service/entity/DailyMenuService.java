package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.DailyMenu;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.DailyMenuRepo;
import sep490.g13.pms_be.repository.UserRepo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class DailyMenuService {
    @Autowired
    private DailyMenuRepo dailyMenuRepo;
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    private UserRepo userRepo;


    public void createDailyMenu(DailyMenu dailyMenu) {
        dailyMenuRepo.save(dailyMenu);
    }

    public List<DailyMenu> getDailyMenusByDate(String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        return dailyMenuRepo.findByDate(date);
    }

    public List<DailyMenu> getMonthlyMenu(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1); // Ngày đầu tháng
        LocalDate endDate = yearMonth.atEndOfMonth(); // Ngày cuối tháng
        return dailyMenuRepo.findByDateBetween(startDate, endDate);
    }
}

package sep490.g13.pms_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.DailyMenu;
import sep490.g13.pms_be.service.entity.DailyMenuService;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/pms/daily-menu")
public class DailyMenuController {
    @Autowired
    private DailyMenuService dailyMenuService;

    @PostMapping("/new-daily-menu")
    public ResponseEntity<?> createDailyMenus(@RequestBody List<DailyMenu> dailyMenus) {
        dailyMenus.forEach(dailyMenuService::createDailyMenu);
        return ResponseEntity.ok("Thực đơn đã được lưu thành công!");
    }

    @GetMapping("/get-daily-menu/{date}")
    public ResponseEntity<?> getDailyMenu(@PathVariable String date) {
        return ResponseEntity.ok(dailyMenuService.getDailyMenusByDate(date));
    }

    @GetMapping("/get-monthly-menu/{year}/{month}")
    public ResponseEntity<List<DailyMenu>> getMonthlyMenu(@PathVariable int year, @PathVariable int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<DailyMenu> dailyMenus = dailyMenuService.getMonthlyMenu(yearMonth);
        return ResponseEntity.ok(dailyMenus);
    }

}


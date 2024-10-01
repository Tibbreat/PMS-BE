package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.DailyMenu;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.dailymenu.AddDailyMenuRequest;
import sep490.g13.pms_be.model.request.dailymenu.UpdateDailyMenuRequest;
import sep490.g13.pms_be.model.response.dailymenu.DailyMenuResponse;
import sep490.g13.pms_be.repository.ClassRepo;
import sep490.g13.pms_be.repository.DailyMenuRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyMenuService {
    @Autowired
    private DailyMenuRepo dailyMenuRepo;
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    private UserRepo userRepo;


    public DailyMenu addDailyMenu(AddDailyMenuRequest addDailyMenuRequest) {
        // Tìm lớp học dựa trên classId
        Classes schoolClass = classRepo.findById(addDailyMenuRequest.getClassId())
                .orElseThrow(() -> new DataNotFoundException("Class not found with id: " + addDailyMenuRequest.getClassId()));

        // Tìm người dùng tạo thực đơn
        User createdBy = userRepo.findById(addDailyMenuRequest.getCreatedById())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + addDailyMenuRequest.getCreatedById()));
        if (createdBy.getRole() != RoleEnums.ADMIN && createdBy.getRole() != RoleEnums.KITCHEN_MANAGER) {
            throw new PermissionNotAcceptException("Phải là admin hoặc kitchen manager");
        } else {
            // Tạo thực đơn hàng ngày mới
            DailyMenu newDailyMenu = new DailyMenu();
            newDailyMenu.setSchoolClasses(schoolClass);
            newDailyMenu.setMenuDate(addDailyMenuRequest.getMenuDate());
            newDailyMenu.setBreakfast(addDailyMenuRequest.getBreakfast());
            newDailyMenu.setLunch(addDailyMenuRequest.getLunch());
            newDailyMenu.setSnacks(addDailyMenuRequest.getSnacks());
            newDailyMenu.setNote(addDailyMenuRequest.getNote());
            newDailyMenu.setCreatedBy(createdBy.getId());  // Set thông tin người tạo

            // Lưu thực đơn vào cơ sở dữ liệu
            return dailyMenuRepo.save(newDailyMenu);
        }

    }
    public Page<DailyMenuResponse> getDailyMenus(String classId, LocalDate menuDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DailyMenu> dailyMenus = dailyMenuRepo.findDailyMenuByFilters(classId, menuDate, pageable);
        // Chuyển đổi từ DailyMenu sang DailyMenuResponse
        List<DailyMenuResponse> dailyMenuResponses = dailyMenus.stream().map(dailyMenu -> DailyMenuResponse.builder()
                .classId(dailyMenu.getSchoolClasses().getId()) // Chỉ lấy classId
                .menuDate(dailyMenu.getMenuDate())
                .breakfast(dailyMenu.getBreakfast())
                .lunch(dailyMenu.getLunch())
                .snacks(dailyMenu.getSnacks())
                .note(dailyMenu.getNote())
                .build()).collect(Collectors.toList());

        // Trả về một đối tượng Page<DailyMenuResponse> mới
        return new PageImpl<>(dailyMenuResponses, pageable, dailyMenus.getTotalElements());
    }
    @Transactional
    public void changeDailyMenuDescription(String menuId, UpdateDailyMenuRequest updateRequest) {
        // Lấy dailyMenu từ database
        DailyMenu existingMenu = dailyMenuRepo.findById(menuId)
                .orElseThrow(() -> new DataNotFoundException("Daily menu not found with id: " + menuId));

        // Lấy user để kiểm tra quyền hạn
        User lastModifiedBy = userRepo.findById(updateRequest.getLastModifiedById())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + updateRequest.getLastModifiedById()));

        // Kiểm tra quyền hạn của user (nếu cần)
        if (!lastModifiedBy.getRole().equals(RoleEnums.ADMIN) && !lastModifiedBy.getRole().equals(RoleEnums.KITCHEN_MANAGER)) {
            throw new PermissionNotAcceptException("Only admins can modify daily menus");
        }

        // Cập nhật các trường của dailyMenu
        if (updateRequest.getBreakfast() != null) {
            existingMenu.setBreakfast(updateRequest.getBreakfast());
        }
        if (updateRequest.getLunch() != null) {
            existingMenu.setLunch(updateRequest.getLunch());
        }
        if (updateRequest.getSnacks() != null) {
            existingMenu.setSnacks(updateRequest.getSnacks());
        }
        if (updateRequest.getNote() != null) {
            existingMenu.setNote(updateRequest.getNote());
        }

        // Cập nhật người sửa cuối cùng
        existingMenu.setLastModifiedBy(updateRequest.getLastModifiedById());

        // Lưu lại thay đổi vào database
        dailyMenuRepo.save(existingMenu);
    }

}

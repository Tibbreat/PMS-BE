package sep490.g13.pms_be.service.entity;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.DailyMealInfo;
import sep490.g13.pms_be.entities.DailyMenu;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.dailymealinfo.AddDailyMealInfoRequest;
import sep490.g13.pms_be.model.request.dailymealinfo.UpdateDailyMealRequest;
import sep490.g13.pms_be.model.response.dailymealinfo.DailyMealInfoResponse;
import sep490.g13.pms_be.repository.ChildrenRepo;
import sep490.g13.pms_be.repository.DailyMealInfoRepo;
import sep490.g13.pms_be.repository.DailyMenuRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyMealInfoService {

    @Autowired
    private DailyMealInfoRepo dailyMealInfoRepository;

    @Autowired
    private ChildrenRepo childrenRepository;

    @Autowired
    private DailyMenuRepo dailyMenuRepository;

    @Autowired
    private UserRepo userRepository;


    public DailyMealInfo addDailyMealInfo(AddDailyMealInfoRequest request) {
        // Find the child by childId
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new DataNotFoundException("Child not found with id: " + request.getChildId()));

        // Find the daily menu by dailyMenuId
        DailyMenu dailyMenu = dailyMenuRepository.findById(request.getDailyMenuId())
                .orElseThrow(() -> new DataNotFoundException("Daily menu not found with id: " + request.getDailyMenuId()));

        // Check if the user who created is valid
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + request.getCreatedById()));
        if (createdBy.getRole() != RoleEnums.ADMIN && createdBy.getRole() != RoleEnums.CLASS_MANAGER) {
            throw new PermissionNotAcceptException("Phải là admin hoặc class manager");}
        if(createdBy.getRole().equals(RoleEnums.CLASS_MANAGER)) {
            if(child.getSchoolClass().getManager().getId() != createdBy.getId()){
                throw new PermissionNotAcceptException("Phải là Class Manager của lớp");
            };
        }
        // Create a new DailyMealInfo entity
        DailyMealInfo dailyMealInfo = new DailyMealInfo();
        dailyMealInfo.setChild(child);
        dailyMealInfo.setDailyMenu(dailyMenu);
        dailyMealInfo.setIsOptedOut(request.getIsOptedOut());
        dailyMealInfo.setReasonForOptOut(request.getReasonForOptOut());

        // Set the creator
        dailyMealInfo.setCreatedBy(request.getCreatedById());

        // Save the daily meal info
        return dailyMealInfoRepository.save(dailyMealInfo);
    }
    public Page<DailyMealInfoResponse> getDailyMealInfo(String childId, String dailyMenuId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Gọi repository để lấy danh sách DailyMealInfo theo bộ lọc
        Page<DailyMealInfo> dailyMealInfos = dailyMealInfoRepository.findDailyMealInfoByChildIdAndMenuId(childId, dailyMenuId, pageable);

        // Chuyển đổi từ DailyMealInfo sang DailyMealInfoResponse
        List<DailyMealInfoResponse> dailyMealInfoResponses = dailyMealInfos.stream()
                .map(dailyMealInfo -> DailyMealInfoResponse.builder()
                        .childId(dailyMealInfo.getChild() != null ? dailyMealInfo.getChild().getId() : null) // Lấy childId
                        .dailyMenuId(dailyMealInfo.getDailyMenu() != null ? dailyMealInfo.getDailyMenu().getId() : null) // Lấy dailyMenuId
                        .isOptedOut(dailyMealInfo.getIsOptedOut())
                        .reasonForOptOut(dailyMealInfo.getReasonForOptOut())
                        .build())
                .collect(Collectors.toList());

        // Trả về một đối tượng Page<DailyMealInfoResponse> mới
        return new PageImpl<>(dailyMealInfoResponses, pageable, dailyMealInfos.getTotalElements());
    }

    public void changeDailyMealDescription(String mealInfoId, UpdateDailyMealRequest updateRequest) {
        // Tìm kiếm DailyMealInfo theo mealInfoId
        DailyMealInfo dailyMealInfo = dailyMealInfoRepository.findById(mealInfoId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin bữa ăn với ID: " + mealInfoId));

        // Cập nhật các trường cần thiết từ request
        if (updateRequest.getIsOptedOut() != null) {
            dailyMealInfo.setIsOptedOut(updateRequest.getIsOptedOut()); // Cập nhật tình trạng tham gia
        }

        if (updateRequest.getReasonForOptOut() != null) {
            dailyMealInfo.setReasonForOptOut(updateRequest.getReasonForOptOut()); // Cập nhật lý do không tham gia
        }

        User lastModifiedBy = userRepository.findById(updateRequest.getLastModifiedByID())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + updateRequest.getLastModifiedByID()));
        if (lastModifiedBy.getRole() != RoleEnums.ADMIN && lastModifiedBy.getRole() != RoleEnums.CLASS_MANAGER) {
            throw new PermissionNotAcceptException("Phải là admin hoặc class manager");}
        if(lastModifiedBy.getRole().equals(RoleEnums.CLASS_MANAGER)) {
            if(dailyMealInfo.getChild().getSchoolClass().getManager().getId() != lastModifiedBy.getId()){
                throw new PermissionNotAcceptException("Phải là Class Manager của lớp");
            };
        }

        // Lưu thay đổi
        dailyMealInfoRepository.save(dailyMealInfo);
    }


}

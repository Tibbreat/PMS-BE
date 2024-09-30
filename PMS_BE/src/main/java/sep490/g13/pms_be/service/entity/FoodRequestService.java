package sep490.g13.pms_be.service.entity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.FoodRequest;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.food.AddFoodRequest;
import sep490.g13.pms_be.model.request.food.UpdateFoodRequest;
import sep490.g13.pms_be.repository.FoodRequestRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.enums.RoleEnums;

@Service
public class FoodRequestService {

    @Autowired
    private FoodRequestRepo foodRequestRepository;
    @Autowired
    private UserRepo userRepository;

    public FoodRequest addFoodRequest(AddFoodRequest addFoodRequest) {



        User createdBy = userRepository.findById(addFoodRequest.getCreatedById())
                 .orElseThrow(() -> new IllegalArgumentException("User not found"));

        FoodRequest foodRequest = new FoodRequest();
        foodRequest.setRequestDate(addFoodRequest.getRequestDate());
        foodRequest.setItem(addFoodRequest.getItem());
        foodRequest.setQuantity(addFoodRequest.getQuantity());
        foodRequest.setUnit(addFoodRequest.getUnit());
        foodRequest.setDeliveryAddress(addFoodRequest.getDeliveryAddress());
        foodRequest.setNote(addFoodRequest.getNote());
        if(createdBy != null) {
            if(createdBy.getRole()!= RoleEnums.ADMIN && createdBy.getRole()!= RoleEnums.KITCHEN_MANAGER) {
                throw new PermissionNotAcceptException("Không được tạo food bằng các role khác");
            }
            else{
                foodRequest.setCreatedBy(createdBy.getId());

            }
        }

        return foodRequestRepository.save(foodRequest);
    }

    public Page<FoodRequest> getFood(String item,  int page, int size) {
        // Tạo Pageable để phân trang
        Pageable pageable = PageRequest.of(page, size);
        // Gọi repository để lấy danh sách lớp học theo bộ lọc
        return foodRequestRepository.findFoodByItem(item, pageable);
    }
    @Transactional
    public void updateFoodRequest(String foodRequestId, UpdateFoodRequest updateFoodRequest) {
        // Tìm kiếm FoodRequest hiện có
        FoodRequest existingFoodRequest = foodRequestRepository.findById(foodRequestId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy Food request với id : " + foodRequestId));

        // Cập nhật các trường trong FoodRequest
        existingFoodRequest.setRequestDate(updateFoodRequest.getRequestDate());
        existingFoodRequest.setItem(updateFoodRequest.getItem());
        existingFoodRequest.setQuantity(updateFoodRequest.getQuantity());
        existingFoodRequest.setUnit(updateFoodRequest.getUnit());
        existingFoodRequest.setDeliveryAddress(updateFoodRequest.getDeliveryAddress());
        existingFoodRequest.setNote(updateFoodRequest.getNote());

        // Tìm người dùng cuối cùng sửa đổi và kiểm tra quyền
        User lastModifiedBy = userRepository.findById(updateFoodRequest.getLastModifiedById())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy user với id: " + updateFoodRequest.getLastModifiedById()));

        if (lastModifiedBy.getRole() != RoleEnums.ADMIN && lastModifiedBy.getRole()!= RoleEnums.KITCHEN_MANAGER) {
            throw new PermissionNotAcceptException("Chỉ ADMIN và Kitchen_manager mới có quyền cập nhật yêu cầu thực phẩm");
        } else {
            existingFoodRequest.setLastModifiedBy(lastModifiedBy.getId());
        }

        // Lưu FoodRequest sau khi cập nhật
        foodRequestRepository.save(existingFoodRequest);
    }

}
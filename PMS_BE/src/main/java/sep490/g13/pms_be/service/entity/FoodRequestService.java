package sep490.g13.pms_be.service.entity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.FoodRequest;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.food.FoodRequestDTO;
import sep490.g13.pms_be.repository.FoodRequestRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.utils.enums.RoleEnums;

@Service
public class FoodRequestService {

    @Autowired
    private FoodRequestRepo foodRequestRepository;
    @Autowired
    private UserRepo userRepository;

    public FoodRequest addFoodRequest(FoodRequestDTO foodRequestDTO) {



        User createdBy = userRepository.findById(foodRequestDTO.getCreatedById())
                 .orElseThrow(() -> new IllegalArgumentException("User not found"));

        FoodRequest foodRequest = new FoodRequest();
        foodRequest.setRequestDate(foodRequestDTO.getRequestDate());
        foodRequest.setItem(foodRequestDTO.getItem());
        foodRequest.setQuantity(foodRequestDTO.getQuantity());
        foodRequest.setUnit(foodRequestDTO.getUnit());
        foodRequest.setDeliveryAddress(foodRequestDTO.getDeliveryAddress());
        foodRequest.setNote(foodRequestDTO.getNote());
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
}
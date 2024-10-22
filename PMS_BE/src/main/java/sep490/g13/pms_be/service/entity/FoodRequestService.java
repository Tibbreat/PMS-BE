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


}
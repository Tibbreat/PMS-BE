package sep490.g13.pms_be.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.model.request.food.FoodRequestDTO;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.service.entity.FoodRequestService;
import sep490.g13.pms_be.entities.FoodRequest;

@RestController
@RequestMapping("/pms/food")
public class FoodController {

    @Autowired
    private FoodRequestService foodRequestService;

    @PostMapping("/add")
    public ResponseEntity<ResponseModel<FoodRequest>> addFoodRequest(@RequestBody FoodRequestDTO foodRequestDTO) {
        FoodRequest foodRequest = foodRequestService.addFoodRequest(foodRequestDTO);
        ResponseModel<FoodRequest> request = ResponseModel.<FoodRequest>builder()
                .message("Food request added successfully")
                .data(foodRequest)
                .build();
        return ResponseEntity.ok(request);
    }
}

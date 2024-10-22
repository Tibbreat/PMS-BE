package sep490.g13.pms_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.FoodRequest;
import sep490.g13.pms_be.model.request.food.AddFoodRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.food.ListFoodResponse;
import sep490.g13.pms_be.model.response.food.ListRequestItemsResponse;
import sep490.g13.pms_be.service.entity.FoodRequestService;

import java.util.List;


@RestController
@RequestMapping("/pms/food-request")
public class FoodRequestController {

    @Autowired
    private FoodRequestService foodRequestService;


    @PostMapping("/add")
    public ResponseEntity<FoodRequest> add(@RequestBody AddFoodRequest request) {
        return ResponseEntity.ok(foodRequestService.add(request));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<PagedResponseModel<ListFoodResponse>> getAllByProviderId(@PathVariable String providerId, @RequestParam int page) {
        Page<ListFoodResponse> foodRequests = foodRequestService.getAllByProviderId(providerId, page - 1, 10);
        List<ListFoodResponse> results = foodRequests.getContent();
        String msg = results.isEmpty() ? "No data found" : "Get all data successfully";
        return ResponseEntity.ok(PagedResponseModel.<ListFoodResponse>builder()
                .page(page)
                .size(10)
                .msg(msg)
                .total(foodRequests.getTotalElements())
                .listData(results)
                .build());
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<ListRequestItemsResponse>> getById(@PathVariable String requestId) {
        return ResponseEntity.ok(foodRequestService.getItems(requestId));
    }

    @PutMapping("/accept/{requestId}")
    public ResponseEntity<Integer> changeStatus(@PathVariable String requestId, @RequestParam String status) {
        return ResponseEntity.ok(foodRequestService.changeStatusOfRequest(requestId, status));
    }
}

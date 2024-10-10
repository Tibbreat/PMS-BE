package sep490.g13.pms_be.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sep490.g13.pms_be.entities.Vehicle;
import sep490.g13.pms_be.entities.Vehicle;
import sep490.g13.pms_be.model.request.vehicle.AddVehicleRequest;
import sep490.g13.pms_be.model.request.vehicle.UpdateVehicleRequest;
import sep490.g13.pms_be.model.response.base.PagedResponseModel;
import sep490.g13.pms_be.model.response.base.ResponseModel;
import sep490.g13.pms_be.model.response.vehicle.VehicleDetailResponse;
import sep490.g13.pms_be.service.entity.VehicleService;
import sep490.g13.pms_be.utils.ValidationUtils;

import java.util.List;

@RestController
@RequestMapping("/pms/vehicle")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;
    @PostMapping("/add")
    public ResponseEntity<ResponseModel<?>> addVehicle(
            @Valid @RequestBody AddVehicleRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String validationErrors = ValidationUtils.getValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(
                    ResponseModel.builder()
                            .message("Vehicle information is invalid")
                            .data(validationErrors)
                            .build()
            );
        }

        try {
            // Call service to add vehicle
            Vehicle savedVehicle = vehicleService.addVehicle(request);

            return ResponseEntity.ok(
                    ResponseModel.builder()
                            .message("Vehicle added successfully")
                            .data(request)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseModel.builder()
                            .message("An error occurred while adding the vehicle: " + e.getMessage())
                            .build()
            );
        }
    }
    @GetMapping
    public ResponseEntity<PagedResponseModel<Vehicle>> getVehicles(
            @RequestParam int page,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String transportProviderId) {

        int size = 10; // Number of vehicles per page
        Page<Vehicle> result = vehicleService.getVehiclesByFilters(model, brand, transportProviderId, page - 1, size);

        // Initialize any required lazy-loaded associations if necessary
        // For example:
        // result.getContent().forEach(vehicle -> Hibernate.initialize(vehicle.getTransport()));

        List<Vehicle> vehicleList = result.getContent();

        // Create response with pagination
        PagedResponseModel<Vehicle> pagedResponse = PagedResponseModel.<Vehicle>builder()
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .listData(vehicleList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }
    @GetMapping("/vehicle-detail/{vehicleId}")
    public ResponseEntity<ResponseModel<?>> getvehicleDetail(@PathVariable String vehicleId) {
        VehicleDetailResponse vehicleDetailResponse = vehicleService.getVehicleById(vehicleId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseModel.<VehicleDetailResponse>builder()
                        .message("Lấy dữ liệu xe có id: " + vehicleId)
                        .data(vehicleDetailResponse)
                        .build());
    }
    @PutMapping("/change-vehicle-information/{vehicleId}")
    public ResponseEntity<ResponseModel<?>> updateVehicle(
            @RequestBody @Valid UpdateVehicleRequest updateVehicleRequest,
            BindingResult bindingResult,
            @PathVariable String vehicleId) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(ResponseModel.<String>builder()
                            .message("Update failed")
                            .data(ValidationUtils.getValidationErrors(bindingResult))
                            .build());
        }

        vehicleService.updateVehicleInformation(vehicleId, updateVehicleRequest);

        return ResponseEntity.ok(ResponseModel.<String>builder()
                .message("Vehicle information updated successfully")
                .data(null)
                .build());
    }
    @PutMapping("/change-vehicle-status/{vehicleId}")
    public ResponseEntity<ResponseModel<Vehicle>> changeVehicleStatus(@PathVariable String vehicleId, @RequestParam Boolean newStatus) {
        try {
            Vehicle updatedVehicle = vehicleService.changeVehicleStatus(vehicleId, newStatus);
            return ResponseEntity.ok(ResponseModel.<Vehicle>builder().data(updatedVehicle).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}

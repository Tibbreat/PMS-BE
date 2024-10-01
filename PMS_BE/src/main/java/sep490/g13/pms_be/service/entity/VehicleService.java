package sep490.g13.pms_be.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.TransportServiceProvider;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.entities.Vehicle;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.vehicle.AddVehicleRequest;
import sep490.g13.pms_be.model.request.vehicle.UpdateVehicleRequest;
import sep490.g13.pms_be.model.response.vehicle.VehicleDetailResponse;
import sep490.g13.pms_be.repository.TransportServiceProviderRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.repository.VehicleRepo;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TransportServiceProviderRepo transportRepo;

    public Vehicle addVehicle(AddVehicleRequest vehicleDTO) {

        // Find user by createdBy ID
        User createdByUser = userRepo.findById(vehicleDTO.getCreatedById())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + vehicleDTO.getCreatedById()));

        // Check user permissions (e.g., ADMIN or MANAGER)
        if (!isAuthorizedToCreateVehicle(createdByUser)) {
            throw new PermissionNotAcceptException("Just Admin and Manager can create vehicles");
        }

        // Find transport service provider
        TransportServiceProvider transport = transportRepo.findById(vehicleDTO.getTransportServiceProviderId())
                .orElseThrow(() -> new DataNotFoundException("TransportServiceProvider not found with id: " + vehicleDTO.getTransportServiceProviderId()));

        // Create and populate Vehicle object
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(vehicleDTO.getLicensePlate());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setBrand(vehicleDTO.getBrand());
        vehicle.setTransport(transport);
        vehicle.setCreatedBy(createdByUser.getId());

        // Save and return the vehicle
        return vehicleRepo.save(vehicle);
    }

    private boolean isAuthorizedToCreateVehicle(User user) {
        return user.getRole() == RoleEnums.ADMIN || user.getRole() == RoleEnums.TRANSPORT_MANAGER;
    }
    public Page<Vehicle> getVehiclesByFilters(String model, String brand, String transportProviderId, int page, int size) {
        // Create Pageable for pagination
        Pageable pageable = PageRequest.of(page, size);
        // Call repository to get the list of vehicles based on filters
        return vehicleRepo.findVehiclesByFilters(model, brand, transportProviderId, pageable);
    }
    public VehicleDetailResponse getVehicleById(String vehicleId) {
        // Find the vehicle by ID
        Optional<Vehicle> vehicleOptional = vehicleRepo.findById(vehicleId);

        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();

            // Create and return VehicleDetailResponse
            return VehicleDetailResponse.builder()
                    .licensePlate(vehicle.getLicensePlate())
                    .color(vehicle.getColor())
                    .model(vehicle.getModel())
                    .brand(vehicle.getBrand())
                    .transportProviderId(vehicle.getTransport().getId()) // Get ID of the transport provider
                    .build();
        } else {
            throw new RuntimeException("Vehicle not found with ID: " + vehicleId);
        }
    }
    @Transactional
    public Vehicle updateVehicleInformation(String vehicleId, UpdateVehicleRequest updateVehicleRequest) {
        Vehicle existingVehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new DataNotFoundException("Vehicle not found with id: " + vehicleId));

        existingVehicle.setLicensePlate(updateVehicleRequest.getLicensePlate());
        existingVehicle.setColor(updateVehicleRequest.getColor());
        existingVehicle.setModel(updateVehicleRequest.getModel());
        existingVehicle.setBrand(updateVehicleRequest.getBrand());

        TransportServiceProvider transportProvider = transportRepo.findById(updateVehicleRequest.getTransportProviderId())
                .orElseThrow(() -> new DataNotFoundException("Transport provider not found with id: " + updateVehicleRequest.getTransportProviderId()));
        existingVehicle.setTransport(transportProvider);

        User lastModifiedBy = userRepo.findById(updateVehicleRequest.getLastModifiedById())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + updateVehicleRequest.getLastModifiedById()));
        if (!lastModifiedBy.getRole().equals(RoleEnums.ADMIN) && !lastModifiedBy.getRole().equals(RoleEnums.TRANSPORT_MANAGER)) {
            throw new PermissionNotAcceptException("Only ADMIN and Transport Manager can modify this vehicle.");
        }

        return vehicleRepo.save(existingVehicle);
    }
}

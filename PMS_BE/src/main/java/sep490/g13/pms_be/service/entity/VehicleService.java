package sep490.g13.pms_be.service.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.TransportServiceProvider;
import sep490.g13.pms_be.entities.Vehicle;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.model.request.vehicle.AddVehicleRequest;
import sep490.g13.pms_be.model.request.vehicle.AvailableVehicleOptions;
import sep490.g13.pms_be.repository.ChildrenVehicleRegistrationRepo;
import sep490.g13.pms_be.repository.TransportServiceProviderRepo;
import sep490.g13.pms_be.repository.VehicleRepo;

import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private TransportServiceProviderRepo transportRepo;

    @Autowired
    private ChildrenVehicleRegistrationRepo childrenVehicleRegistrationRepo;


    public Page<Vehicle> getAllVehicle(String providerId, int page, int size) {
        return vehicleRepo.findAllByTransportId(providerId, PageRequest.of(page, size));
    }

    public Vehicle add(AddVehicleRequest request) {
        TransportServiceProvider provider = transportRepo.findById(request.getProviderId()).orElseThrow(() -> new DataNotFoundException("Provider not found"));
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(request, vehicle);
        vehicle.setCreatedBy(request.getCreatedBy());
        vehicle.setTransport(provider);
        vehicle.setTimeStart("7:00");
        vehicle.setIsActive(Boolean.TRUE);
        return vehicleRepo.save(vehicle);
    }

    public List<AvailableVehicleOptions> getAvailableVehicle(){
        return vehicleRepo.findAllAvailableVehicle();
    }
//    public List<AvailableVehicleOptions> getAvailableVehicle(String childId) {
//        Children child = childrenRepo.findById(childId)
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trẻ với ID: " + childId));
//
//        String childAddress = child.getChildAddress();
//
//        List<AvailableVehicleOptions> availableVehicles = vehicleRepo.findAllAvailableVehicle();
//
//        availableVehicles.forEach(vehicle -> {
//            System.out.println("Địa chỉ của trẻ: " + childAddress);
//            System.out.println("Địa chỉ đưa đón: " + vehicle.getPickUpLocation());
//
//            double distance = distanceService.calculateDistance(childAddress, vehicle.getPickUpLocation());
////            double distance = distanceService.calculateDistance(childAddress, vehicle.getPickUpLocation());
//            vehicle.setDistance(distance);  // Gán khoảng cách vào đối tượng
//
//        });
//
//        return availableVehicles;
//    }
}

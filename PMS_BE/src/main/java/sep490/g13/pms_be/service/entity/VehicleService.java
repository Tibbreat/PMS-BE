package sep490.g13.pms_be.service.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sep490.g13.pms_be.entities.TransportServiceProvider;
import sep490.g13.pms_be.entities.Vehicle;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.model.request.vehicle.AddVehicleRequest;
import sep490.g13.pms_be.repository.TransportServiceProviderRepo;
import sep490.g13.pms_be.repository.VehicleRepo;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private TransportServiceProviderRepo transportRepo;


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

}

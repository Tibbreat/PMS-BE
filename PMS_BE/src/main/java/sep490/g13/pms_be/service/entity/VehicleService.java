package sep490.g13.pms_be.service.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.Children;
import sep490.g13.pms_be.entities.TransportServiceProvider;
import sep490.g13.pms_be.entities.Vehicle;
import sep490.g13.pms_be.entities.VehicleImage;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.model.request.vehicle.AddVehicleRequest;
import sep490.g13.pms_be.model.request.vehicle.AvailableVehicleOptions;
import sep490.g13.pms_be.repository.ChildrenVehicleRegistrationRepo;
import sep490.g13.pms_be.repository.TransportServiceProviderRepo;
import sep490.g13.pms_be.repository.VehicleImageRepo;
import sep490.g13.pms_be.repository.VehicleRepo;
import sep490.g13.pms_be.service.utils.CloudinaryService;

import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private TransportServiceProviderRepo transportRepo;

    @Autowired
    private ChildrenVehicleRegistrationRepo childrenVehicleRegistrationRepo;

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private VehicleImageRepo vehicleImageRepo;


    public Page<Vehicle> getAllVehicle(String providerId, int page, int size) {
        return vehicleRepo.findAllByTransportId(providerId, PageRequest.of(page, size));
    }

    public Vehicle add(AddVehicleRequest request, List<MultipartFile> images) {
        // Kiểm tra xem provider có tồn tại không
        TransportServiceProvider provider = transportRepo.findById(request.getProviderId())
                .orElseThrow(() -> new DataNotFoundException("Provider not found"));

        // Kiểm tra số lượng phương tiện của provider
        int count = vehicleRepo.countByTransport_Id(request.getProviderId());
        if (count >= provider.getTotalVehicle()) {
            throw new IllegalArgumentException("Provider has reached maximum number of vehicle");
        }

        // Tạo đối tượng Vehicle mới
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(request, vehicle);
        vehicle.setCreatedBy(request.getCreatedBy());
        vehicle.setTransport(provider);
        vehicle.setTimeStart("7:00");
        vehicle.setIsActive(Boolean.TRUE);

        // Lưu phương tiện vào cơ sở dữ liệu trước
        Vehicle savedVehicle = vehicleRepo.save(vehicle);

        // Xử lý tệp hình ảnh (nếu có)
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        // Upload ảnh lên Cloudinary và nhận URL
                        String imagePath = cloudinaryService.saveImage(image);

                        // Tạo đối tượng VehicleImage và liên kết với Vehicle
                        VehicleImage vehicleImage = new VehicleImage();
                        vehicleImage.setImageUrl(imagePath);
                        vehicleImage.setImageType(image.getContentType());
                        vehicleImage.setVehicle(savedVehicle);

                        // Lưu hình ảnh vào cơ sở dữ liệu
                        vehicleImageRepo.save(vehicleImage);

                    } catch (Exception e) {
                        throw new RuntimeException("Error uploading vehicle image: " + e.getMessage());
                    }
                }
            }
        }

        // Trả về đối tượng Vehicle đã lưu, cùng với các hình ảnh nếu có
        return savedVehicle;
    }

    public List<AvailableVehicleOptions> getAvailableVehicle(){
        return vehicleRepo.findAllAvailableVehicle();
    }

    @Transactional
    public void updateStatus(String vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new DataNotFoundException("Vehicle not found"));

        vehicleRepo.updateStatus(vehicleId, !vehicle.getIsActive());
    }
}

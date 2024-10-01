package sep490.g13.pms_be.service.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.FoodServiceProvider;
import sep490.g13.pms_be.entities.TransportServiceProvider;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.foodsupplier.UpdateFoodProviderRequest;
import sep490.g13.pms_be.model.request.transportsupplier.AddTransportProviderRequest;
import sep490.g13.pms_be.model.request.transportsupplier.UpdateTransportRequest;
import sep490.g13.pms_be.repository.TransportServiceProviderRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.utils.DriveService;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.io.File;
import java.io.IOException;

@Service
public class TransportServiceProviderService {

    @Autowired
    private TransportServiceProviderRepo transportServiceProviderRepo;

    @Autowired
    private DriveService driveService;

    @Autowired
    private UserRepo userRepo;

    @Transactional
    public TransportServiceProvider addTransportProvider(AddTransportProviderRequest tpa, MultipartFile contractFile) throws IOException {
        TransportServiceProvider newTransportProvider = new TransportServiceProvider();
        BeanUtils.copyProperties(tpa, newTransportProvider);

        // Kiểm tra người tạo
        if (tpa.getCreatedBy() == null) {
            throw new IllegalArgumentException("Người tạo không xác định hoặc null");
        }

        // Tìm kiếm user được tạo
        User createdByUser = userRepo.findById(tpa.getCreatedBy())
                .orElseThrow(() -> new DataNotFoundException("Người dùng với id: " + tpa.getCreatedBy() + "không được tìm thấy"));
        newTransportProvider.setCreatedBy(tpa.getCreatedBy());

        // Ensure that only ADMIN role can create a class
        if (createdByUser.getRole() != RoleEnums.ADMIN) {
            throw new PermissionNotAcceptException("Chỉ có thể tạo nhà cung cấp thức ăn với quyền Admin");
        }

        // Xử lý tệp hợp đồng
        if (contractFile != null && !contractFile.isEmpty()) {
            // Tạo tên file tạm với slug và đuôi file phù hợp
            File tempFile = File.createTempFile("Hợp Đồng-" + tpa.getProviderName() + "-", ".pdf");

            // Chuyển nội dung file từ MultipartFile sang file tạm
            contractFile.transferTo(tempFile);

            // Chọn folder tương ứng để upload (ví dụ: folder cho nhà cung cấp thức ăn)
            String folderId = "1_6jB1E5nbSKTO-zBq_4Cs4wHUlRakJDa";

            // Upload file lên Google Drive
            String contractLink = driveService.upload(tempFile, folderId);
            newTransportProvider.setContractFile(contractLink);

            // Xóa file tạm sau khi upload
            if (!tempFile.delete()) {
                System.out.println("Cảnh báo: Không xóa được tệp tạm thời.");
            }
        }
        return transportServiceProviderRepo.save(newTransportProvider);
    }

    public Page<TransportServiceProvider> getTransportProvider(Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transportServiceProviderRepo.findByFilter(status, pageable);
    }

    @Transactional
    public void updateTransportProvider(UpdateTransportRequest updateTransportRequest, String transportProviderId) {

        TransportServiceProvider transportServiceProvider = transportServiceProviderRepo.findById(transportProviderId)
                .orElseThrow(() -> new DataNotFoundException("Nhà cung cấp dịch vụ vận chuyển với id " + transportProviderId + " không được tìm thấy"));

        User lastMofifyBy = userRepo.findById(updateTransportRequest.getLastModifyById()).get();
        if(lastMofifyBy.getRole() != RoleEnums.ADMIN) {
            throw new PermissionNotAcceptException("Không thể cập nhật nhà cung cấp dịch vụ vận chuyển với quyền khác");
        }else {
            transportServiceProvider.setLastModifiedBy(updateTransportRequest.getLastModifyById());
        }
        transportServiceProviderRepo.updateTransportProvider(updateTransportRequest, transportProviderId);
    }

    public TransportServiceProvider updateStatus(String transportProviderId, Boolean status) {
        TransportServiceProvider transportServiceProvider = transportServiceProviderRepo.findById(transportProviderId).orElseThrow(()
                -> new DataNotFoundException("Không tìm thấy nhà cung cấp nào"));
        transportServiceProvider.setIsActive(status);
        return transportServiceProviderRepo.save(transportServiceProvider);
    }
}

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
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.exception.other.PermissionNotAcceptException;
import sep490.g13.pms_be.model.request.foodsupplier.AddFoodProviderRequest;
import sep490.g13.pms_be.model.request.foodsupplier.UpdateFoodProviderRequest;
import sep490.g13.pms_be.repository.FoodServiceProviderRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.utils.DriveService;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Service
public class FoodServiceProviderService {

    @Autowired
    private FoodServiceProviderRepo foodServiceProviderRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DriveService driveService;

    @Transactional
    public FoodServiceProvider addFoodProvider(AddFoodProviderRequest fpa, MultipartFile contractFile) throws IOException {
        FoodServiceProvider newFoodProvider = new FoodServiceProvider();
        BeanUtils.copyProperties(fpa, newFoodProvider);

        // Kiểm tra người tạo
        if (fpa.getCreatedBy() == null) {
            throw new IllegalArgumentException("Người tạo không xác định hoặc null");
        }

        // Tìm kiếm user được tạo
        User createdByUser = userRepo.findById(fpa.getCreatedBy())
                .orElseThrow(() -> new DataNotFoundException("Người dùng với id: " + fpa.getCreatedBy() + "không được tìm thấy"));
        newFoodProvider.setCreatedBy(fpa.getCreatedBy());

        // Ensure that only ADMIN role can create a class
        if (createdByUser.getRole() != RoleEnums.ADMIN) {
            throw new PermissionNotAcceptException("Chỉ có thể tạo nhà cung cấp thức ăn với quyền Admin");
        }

        // Xử lý tệp hợp đồng
        if (contractFile != null && !contractFile.isEmpty()) {
            // Tạo tên file tạm với slug và đuôi file phù hợp
            File tempFile = File.createTempFile("Hợp Đồng-" + fpa.getProviderName() + "-", ".pdf");

            // Chuyển nội dung file từ MultipartFile sang file tạm
            contractFile.transferTo(tempFile);

            // Upload file lên Google Drive
            String contractLink = driveService.upload(tempFile);
            newFoodProvider.setContractFile(contractLink);

            // Xóa file tạm sau khi upload
            if (!tempFile.delete()) {
                System.out.println("Cảnh báo: Không xóa được tệp tạm thời.");
            }
        }
        return foodServiceProviderRepo.save(newFoodProvider);
    }

    public Page<FoodServiceProvider> getProvider(Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return foodServiceProviderRepo.findByProvider(status, pageable);
    }

    @Transactional
    public void updateFoodProvider(UpdateFoodProviderRequest updateFoodProviderRequest, String foodProviderId) {

        FoodServiceProvider foodServiceProvider = foodServiceProviderRepo.findById(foodProviderId)
                .orElseThrow(() -> new DataNotFoundException("Nhà cung cấp thức ăn với id " + foodProviderId + " không được tìm thấy"));

        User lastMofifyBy = userRepo.findById(updateFoodProviderRequest.getLastModifyById()).get();
        if(lastMofifyBy.getRole() != RoleEnums.ADMIN) {
            throw new PermissionNotAcceptException("Không thể cập nhật nhà cung cấp thức ăn với quyền khác");
        }else {
            foodServiceProvider.setLastModifiedBy(updateFoodProviderRequest.getLastModifyById());
        }
        foodServiceProviderRepo.updateFoodProvider(updateFoodProviderRequest, foodProviderId);
    }
}
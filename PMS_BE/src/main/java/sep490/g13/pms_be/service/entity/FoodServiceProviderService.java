package sep490.g13.pms_be.service.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.entities.FoodServiceProvider;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.exception.other.DataNotFoundException;
import sep490.g13.pms_be.model.request.foodsupplier.FoodProviderAddNewRequest;
import sep490.g13.pms_be.repository.FoodServiceProviderRepo;
import sep490.g13.pms_be.repository.UserRepo;
import sep490.g13.pms_be.service.utils.DriveService;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FoodServiceProviderService {

    @Autowired
    private FoodServiceProviderRepo foodServiceProviderRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DriveService driveService;

    //        //Set createBy user
//        User createdByUser = userRepo.findById(fpa.getCreatedBy())
//                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng nào có id: " + fpa.getCreatedBy()));
//        newFoodProvider.setCreatedBy(String.valueOf(createdByUser));

    @Transactional
    public FoodServiceProvider addFoodProvider(FoodProviderAddNewRequest fpa) throws IOException {

        FoodServiceProvider newFoodProvider = new FoodServiceProvider();
        BeanUtils.copyProperties(fpa, newFoodProvider);

        MultipartFile contractFile = fpa.getContractFile();
        if (contractFile != null && !contractFile.isEmpty()) {
            // Chuyển đổi MultipartFile thành File
            File convFile = new File(contractFile.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(contractFile.getBytes());
            }

            // Upload file lên Google Drive
            String contractLink = driveService.upload(convFile);
            newFoodProvider.setContractFile(contractLink);

            // Xoá file sau khi upload
            if (!convFile.delete()) {
                System.out.println("Warning: Temporary file deletion failed.");
            }
        }

        return foodServiceProviderRepo.save(newFoodProvider);
    }




    public Page<FoodServiceProvider> getProvider(Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return foodServiceProviderRepo.findByProvider(status, pageable);
    }
}


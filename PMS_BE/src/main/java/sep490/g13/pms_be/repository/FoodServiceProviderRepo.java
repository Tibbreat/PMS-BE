package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.FoodServiceProvider;
import sep490.g13.pms_be.model.request.foodsupplier.UpdateFoodProviderRequest;

public interface FoodServiceProviderRepo extends JpaRepository<FoodServiceProvider, String> {

    @Query("SELECT fsp FROM FoodServiceProvider fsp WHERE " +
            "(:status IS NULL OR fsp.isActive = :status)")
    Page<FoodServiceProvider> findByProvider(
            @Param("status") Boolean status,
            Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE FoodServiceProvider fsp SET " +
            "fsp.representativeName = :#{#updateFPR.representativeName}, " +
            "fsp.providerName = :#{#updateFPR.providerName}, " +
            "fsp.providerPhone = :#{#updateFPR.providerPhone}, " +
            "fsp.providerEmail = :#{#updateFPR.providerEmail}, " +
            "fsp.providerAddress = :#{#updateFPR.providerAddress}, " +
            "fsp.providerRegisterNumber = :#{#updateFPR.providerRegisterNumber}, " +
            "fsp.providerLicenseNumber = :#{#updateFPR.providerLicenseNumber}," +
            "fsp.lastModifiedBy = :#{#updateFPR.lastModifyById} " +
            "WHERE fsp.id = :foodProviderId")
    void updateFoodProvider(
            @Param("updateFPR") UpdateFoodProviderRequest updateFPR,
            @Param("foodProviderId") String foodProviderId);
}

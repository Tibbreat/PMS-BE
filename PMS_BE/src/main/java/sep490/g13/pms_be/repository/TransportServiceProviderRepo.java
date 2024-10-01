package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.FoodServiceProvider;
import sep490.g13.pms_be.entities.TransportServiceProvider;
import sep490.g13.pms_be.model.request.foodsupplier.UpdateFoodProviderRequest;
import sep490.g13.pms_be.model.request.transportsupplier.UpdateTransportRequest;

public interface TransportServiceProviderRepo extends JpaRepository<TransportServiceProvider, String> {

    @Query("SELECT tsp FROM TransportServiceProvider tsp WHERE " +
            "(:status IS NULL OR tsp.isActive = :status)")
    Page<TransportServiceProvider> findByFilter(
            @Param("status") Boolean status,
            Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE TransportServiceProvider tsp SET " +
            "tsp.representativeName = :#{#updateTransportRequest.representativeName}, " +
            "tsp.providerName = :#{#updateTransportRequest.providerName}, " +
            "tsp.providerPhone = :#{#updateTransportRequest.providerPhone}, " +
            "tsp.providerEmail = :#{#updateTransportRequest.providerEmail}, " +
            "tsp.providerAddress = :#{#updateTransportRequest.providerAddress}, " +
            "tsp.providerRegisterNumber = :#{#updateTransportRequest.providerRegisterNumber}, " +
            "tsp.providerLicenseNumber = :#{#updateTransportRequest.providerLicenseNumber}," +
            "tsp.lastModifiedBy = :#{#updateTransportRequest.lastModifyById}," +
            "tsp.lastModifiedDate = CURRENT_TIMESTAMP " +
            "WHERE tsp.id = :transportProviderId")
    void updateTransportProvider(
            @Param("updateTransportRequest") UpdateTransportRequest updateTransportRequest,
            @Param("transportProviderId") String transportProviderId);
}

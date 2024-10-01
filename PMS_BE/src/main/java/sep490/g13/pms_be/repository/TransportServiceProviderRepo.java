package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sep490.g13.pms_be.entities.FoodServiceProvider;
import sep490.g13.pms_be.entities.TransportServiceProvider;

public interface TransportServiceProviderRepo extends JpaRepository<TransportServiceProvider, String> {

    @Query("SELECT tsp FROM TransportServiceProvider tsp WHERE " +
            "(:status IS NULL OR tsp.isActive = :status)")
    Page<TransportServiceProvider> findByFilter(
            @Param("status") Boolean status,
            Pageable pageable);

}

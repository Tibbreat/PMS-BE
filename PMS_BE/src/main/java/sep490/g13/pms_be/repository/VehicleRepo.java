package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.Vehicle;

@Repository
public interface VehicleRepo extends JpaRepository<Vehicle,String> {
    @Query("SELECT v FROM Vehicle v " +
            "LEFT JOIN FETCH v.transport " +
            "WHERE (:model IS NULL OR v.model = :model) " +
            "AND (:brand IS NULL OR v.brand = :brand) " +
            "AND (:transportProviderId IS NULL OR v.transport.id = :transportProviderId) " +
            "ORDER BY v.licensePlate, v.color")
    Page<Vehicle> findVehiclesByFilters(
            @Param("model") String model,
            @Param("brand") String brand,
            @Param("transportProviderId") String transportProviderId,
            Pageable pageable);
}

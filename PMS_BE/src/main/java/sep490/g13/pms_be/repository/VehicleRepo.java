package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.Vehicle;

@Repository
public interface VehicleRepo extends JpaRepository<Vehicle, String> {

    @Query("SELECT v FROM Vehicle v WHERE v.transport.id = :transportId")
    Page<Vehicle> findAllByTransportId(String transportId, Pageable pageable);
}

package sep490.g13.pms_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.ChildrenVehicleRegistration;

@Repository
public interface ChildrenVehicleRegistrationRepo extends JpaRepository<ChildrenVehicleRegistration, String> {
    @Query("SELECT COUNT(cvr.id) FROM ChildrenVehicleRegistration cvr WHERE cvr.vehicle.id = :vehicleId")
    long countByVehicleId(@Param("vehicleId") String vehicleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChildrenVehicleRegistration cvr WHERE cvr.children.id = :childrenId")
    void deleteByChildrenId(String childrenId);
}

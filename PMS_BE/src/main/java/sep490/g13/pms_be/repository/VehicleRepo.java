package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.Vehicle;
import sep490.g13.pms_be.model.request.vehicle.AvailableVehicleOptions;

import java.util.List;

@Repository
public interface VehicleRepo extends JpaRepository<Vehicle, String> {

    @Query("SELECT v FROM Vehicle v WHERE v.transport.id = :transportId")
    Page<Vehicle> findAllByTransportId(String transportId, Pageable pageable);



    @Query("SELECT new sep490.g13.pms_be.model.request.vehicle.AvailableVehicleOptions(" +
            "v.id, " +
            "v.vehicleName, " +
            "v.numberOfSeats, " +
            "CAST((v.numberOfSeats - COALESCE(SUM(CASE WHEN cvr.id IS NOT NULL THEN 1 ELSE 0 END), 0)) AS int), " +  // Ép kiểu về int
            "v.pickUpLocation, " +
            "v.timeStart," +
            "0 ) " +
            "FROM Vehicle v " +
            "LEFT JOIN ChildrenVehicleRegistration cvr ON cvr.vehicle.id = v.id " +
            "WHERE v.isActive = true " +
            "GROUP BY v.id, v.vehicleName, v.numberOfSeats, v.pickUpLocation, v.timeStart")
    List<AvailableVehicleOptions> findAllAvailableVehicle();

}

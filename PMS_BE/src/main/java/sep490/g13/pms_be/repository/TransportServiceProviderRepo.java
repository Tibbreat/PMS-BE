package sep490.g13.pms_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.TransportServiceProvider;

@Repository
public interface TransportServiceProviderRepo extends JpaRepository<TransportServiceProvider, String> {

}

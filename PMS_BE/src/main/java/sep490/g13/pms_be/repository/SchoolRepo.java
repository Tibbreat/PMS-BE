package sep490.g13.pms_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.School;

@Repository
public interface SchoolRepo extends JpaRepository<School, String> {
    School findByPrincipalId(String principalId);
}

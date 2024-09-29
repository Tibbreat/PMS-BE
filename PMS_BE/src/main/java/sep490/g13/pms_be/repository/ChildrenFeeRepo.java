package sep490.g13.pms_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.ChildrenFee;

@Repository
public interface ChildrenFeeRepo extends JpaRepository<ChildrenFee, String> {

}

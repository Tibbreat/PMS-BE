package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.Classes;
import sep490.g13.pms_be.entities.FoodRequest;

@Repository
public interface FoodRequestRepo extends JpaRepository<FoodRequest, String> {

    @Query("SELECT fr FROM FoodRequest fr " +
            "WHERE (:item IS NULL OR fr.item LIKE %:item%) " +
            "ORDER BY fr.requestDate DESC")
    Page<FoodRequest> findFoodByItem(
            @Param("item") String item,
            Pageable pageable);
}

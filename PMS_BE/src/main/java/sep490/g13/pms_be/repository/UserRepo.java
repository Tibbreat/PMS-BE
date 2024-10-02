package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    User findByUsername(String username);

    @Query("SELECT COUNT(u.id) FROM User u WHERE u.username LIKE %:accountName%")
    int countByUsernameContaining(String accountName);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> getUserByRole(@Param("role") RoleEnums role,
                             @Param("isActive") Boolean isActive,
                             Pageable pageable);

}

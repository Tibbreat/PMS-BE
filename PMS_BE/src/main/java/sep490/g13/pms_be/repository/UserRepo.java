package sep490.g13.pms_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sep490.g13.pms_be.entities.User;
import sep490.g13.pms_be.model.response.user.GetUsersOptionResponse;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    User findByUsername(String username);

    @Query("SELECT COUNT(u.id) FROM User u WHERE u.username LIKE %:accountName%")
    int countByUsernameContaining(String accountName);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:roles IS NULL OR u.role IN :roles) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive) AND " +
            "u.school.id = :schoolId")
    Page<User> getUsersByRoles(@Param("roles") List<RoleEnums> roles,
                               @Param("isActive") Boolean isActive,
                               @Param("schoolId") String schoolId,
                               Pageable pageable);


    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = :status WHERE u.id = :id")
    int updateUserStatus(@Param("id") String id, @Param("status") Boolean status);

    @Query("SELECT new sep490.g13.pms_be.model.response.user.GetUsersOptionResponse(u.id, u.username) FROM User u " +
            "WHERE u.role = :role " +
            "AND u.isActive = true")
    List<GetUsersOptionResponse> findAllByRole(RoleEnums role);

    @Query("SELECT u FROM User u WHERE u.idCardNumber = :idCardNumber")
    User existByIdCardNumber(String idCardNumber);

    @Query("SELECT new sep490.g13.pms_be.model.response.user.GetUsersOptionResponse(u.id, u.username) FROM User u " +
            "WHERE u.role = :role " +
            "AND u.isActive = true " +
            "AND u.username IS NOT NULL")
    List<GetUsersOptionResponse> findAllByRoleWithUserName(RoleEnums role);
}
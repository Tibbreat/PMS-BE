package sep490.g13.pms_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sep490.g13.pms_be.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    User findByUsername(String username);

    int countByFullName(String fullName);
}

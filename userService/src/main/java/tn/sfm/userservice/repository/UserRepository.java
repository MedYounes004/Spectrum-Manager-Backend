package tn.sfm.userservice.repository;

import tn.sfm.userservice.entity.User;
import tn.sfm.userservice.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByStatus(UserStatus status);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
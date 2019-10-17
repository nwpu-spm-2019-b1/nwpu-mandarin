package mandarin.dao;

import mandarin.auth.UserType;
import mandarin.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Page<User> findAllByUsernameContaining(String query, Pageable pageable);

    Page<User> findAllByUsernameContainingAndType(String query, UserType userType, Pageable pageable);

    List<User> findAllByType(UserType type);

    Optional<User> findByEmail(String email);
}
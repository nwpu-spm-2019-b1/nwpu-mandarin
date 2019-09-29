package mandarin.dao;

import mandarin.auth.UserType;
import mandarin.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    List<User> findAllByType(UserType type);
}
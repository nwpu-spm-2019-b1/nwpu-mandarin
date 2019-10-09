package mandarin.dao;

import mandarin.entities.Reservation;
import mandarin.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    Optional<Reservation> findByBookId(Integer bookId);

    List<Reservation> findAllByUser(User user);

    int deleteAllByUserId(Integer userId);

    int deleteAllByUser(User user);
}

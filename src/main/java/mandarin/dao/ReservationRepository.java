package mandarin.dao;

import mandarin.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation,Integer> {
    Optional<Reservation> findByBookId(Integer bookId);
}

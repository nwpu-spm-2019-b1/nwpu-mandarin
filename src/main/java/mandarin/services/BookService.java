package mandarin.services;

import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.ReservationRepository;
import mandarin.entities.Book;
import mandarin.entities.LendingLogItem;
import mandarin.entities.Reservation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class BookService {
    @Resource
    LendingLogRepository lendingLogRepository;

    @Resource
    ReservationRepository reservationRepository;

    @Resource
    BookRepository bookRepository;

    @Transactional
    public boolean checkAvailability(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new IllegalArgumentException("No such book");
        }
        return checkAvailability(book);
    }

    @Transactional
    public boolean checkAvailability(Book book) {
        Reservation reservation = reservationRepository.findByBookId(book.getId()).orElse(null);
        if (reservation != null) {
            if (Duration.between(reservation.getTime(), Instant.now()).compareTo(Duration.ofHours(2)) < 0) {
                return false;
            }
        }
        return lendingLogRepository.findByBookId(book.getId()).stream().noneMatch((LendingLogItem item) -> item.getEndTime() != null);
    }
}

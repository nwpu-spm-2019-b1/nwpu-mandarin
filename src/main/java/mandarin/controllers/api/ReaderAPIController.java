package mandarin.controllers.api;

import mandarin.auth.AuthenticationNeeded;
import mandarin.auth.SessionHelper;
import mandarin.auth.UserType;
import mandarin.dao.BookRepository;
import mandarin.dao.ReservationRepository;
import mandarin.entities.Book;
import mandarin.entities.Reservation;
import mandarin.exceptions.APIException;
import mandarin.services.BookService;
import mandarin.utils.BasicResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/reader")
@AuthenticationNeeded(UserType.Reader)
public class ReaderAPIController {
    @Resource
    BookRepository bookRepository;

    @Resource
    BookService bookService;

    @Resource
    ReservationRepository reservationRepository;

    @Resource
    SessionHelper sessionHelper;

    @PostMapping("/reserve")
    @Transactional
    public ResponseEntity reserveBook(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null || !bookService.checkAvailability(book)) {
            throw new APIException("Book not available");
        }
        Reservation reservation = new Reservation(book, sessionHelper.getCurrentUser());
        reservationRepository.save(reservation);
        return ResponseEntity.ok(BasicResponse.ok().message("Reserved book successfully").data(reservation.getId()));
    }

    @DeleteMapping("/reserve/{id}")
    @Transactional
    public ResponseEntity deleteReservation(@PathVariable Integer id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            throw new APIException("No such reservation");
        }
        if (!reservation.getUser().getId().equals(sessionHelper.getCurrentUser().getId())) {
            throw new APIException("Do not even try this");
        }
        reservationRepository.deleteById(reservation.getId());
        return ResponseEntity.ok(BasicResponse.ok());
    }
}

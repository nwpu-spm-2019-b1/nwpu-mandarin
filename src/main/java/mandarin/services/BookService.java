package mandarin.services;

import mandarin.auth.SessionHelper;
import mandarin.auth.UserType;
import mandarin.dao.ActionLogRepository;
import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.ReservationRepository;
import mandarin.entities.*;
import mandarin.exceptions.APIException;
import mandarin.utils.BasicResponse;
import mandarin.utils.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.security.auth.login.Configuration;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class BookService {
    public enum BookStatus {
        Available,
        Reserved,
        Borrowed
    }

    @Resource
    SessionHelper sessionHelper;

    @Resource
    LendingLogRepository lendingLogRepository;

    @Resource
    ReservationRepository reservationRepository;

    @Resource
    BookRepository bookRepository;

    @Resource
    ActionLogRepository actionLogRepository;

    @Resource
    ConfigurationService configurationService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean checkAvailability(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new IllegalArgumentException("No such book");
        }
        return checkAvailability(book);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BookStatus getBookStatus(Book book) {
        Reservation reservation = reservationRepository.findByBookId(book.getId()).orElse(null);
        LendingLogItem lendingLogItem = lendingLogRepository.findOutstandingByBook(book).orElse(null);
        if (reservation != null) {
            return BookStatus.Reserved;
        } else if (lendingLogItem != null) {
            return BookStatus.Borrowed;
        } else {
            return BookStatus.Available;
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public LendingLogItem lendBook(User borrower, Book book) {
        if (!borrower.getType().equals(UserType.Reader)) {
            throw new RuntimeException("User is not a reader");
        }
        BookStatus status = getBookStatus(book);
        if (status.equals(BookStatus.Reserved) && book.getReservation().getUser().getId().equals(borrower.getId())) {
            reservationRepository.delete(book.getReservation());
            status = BookStatus.Available;
        }
        if (status.equals(BookStatus.Available)) {
            if (lendingLogRepository.findOutstandingByUser(borrower).size() >= 3) {
                throw new RuntimeException("Number of book borrowed by the user exceeded the limit");
            }
            LendingLogItem item = new LendingLogItem(book, borrower);
            lendingLogRepository.save(item);
            return item;
        } else {
            throw new RuntimeException("Book not available");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean checkAvailability(Book book) {
        Reservation reservation = reservationRepository.findByBookId(book.getId()).orElse(null);
        if (reservation != null) {
            if (Duration.between(reservation.getTime(), Instant.now()).compareTo(Duration.ofHours(2)) < 0) {
                return false;
            }
        }
        return lendingLogRepository.findByBookId(book.getId()).stream().noneMatch((LendingLogItem item) -> item.getEndTime() != null);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void returnBook(User user, Book book) {
        LendingLogItem lendingLogItem = lendingLogRepository.findOutstandingByUserAndBook(user, book).orElse(null);
        if (lendingLogItem == null) {
            throw new RuntimeException("No matching lending record");
        }
        if (!lendingLogItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User not the original borrower");
        }
        lendingLogItem.setEndTime(Instant.now());
        Duration duration = Duration.between(lendingLogItem.getStartTime(), lendingLogItem.getEndTime());
        long daysOverdue = duration.toDays() - configurationService.getAsInt("return_period");
        if (daysOverdue > 0) {
            Map<String, Object> info = new HashMap<>();
            info.put("duration", duration.toDays());
            info.put("fine", configurationService.getAsBigDecimal("fine_rate").multiply(BigDecimal.valueOf(daysOverdue)));
            ActionLogItem actionLogItem = new ActionLogItem(user, "PaidFine", info);
            actionLogRepository.save(actionLogItem);
        }
        lendingLogRepository.save(lendingLogItem);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int deleteBook(Book book) {
        return deleteBook(book.getId());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int deleteBook(Integer bookId) {
        Map<String, Object> actionInfo = new HashMap<>();
        Map<String, Object> bookInfo = new HashMap<>();
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new RuntimeException("No such book");
        }
        ObjectUtils.copyFieldsIntoMap(book, bookInfo, "id", "isbn", "title", "description", "author", "location", "price");
        bookInfo.put("categories", book.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        bookInfo.put("lending_log", lendingLogRepository.findByBookId(book.getId()).stream().map((LendingLogItem item) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("user", ObjectUtils.copyFieldsIntoMap(item.getUser(), null, "id", "username"));
            map.put("start", item.getStartTime().toString());
            map.put("end", item.getEndTime() != null ? item.getEndTime().toString() : null);
            return map;
        }).collect(Collectors.toList()));
        actionInfo.put("book", bookInfo);
        actionLogRepository.save(new ActionLogItem(sessionHelper.getCurrentUser(), "DeleteBook", actionInfo));
        return bookRepository.deleteBookById(bookId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Integer checkBorrowedBookNumber(User user) {
        List<LendingLogItem> lendingLogItems = lendingLogRepository.findByUserId(user.getId());
        Integer count = 0;
        for (LendingLogItem lendingLogItem : lendingLogItems) {
            if (lendingLogItem.getEndTime() == null)
                count++;
        }
        return count;
    }

}

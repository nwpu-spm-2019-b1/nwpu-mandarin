package mandarin.services;

import mandarin.auth.SessionHelper;
import mandarin.dao.ActionLogRepository;
import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.dao.ReservationRepository;
import mandarin.entities.*;
import mandarin.utils.ObjectUtils;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.*;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    @Transactional
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
}

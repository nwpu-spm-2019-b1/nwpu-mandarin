package mandarin.services;

import mandarin.dao.BookRepository;
import mandarin.dao.LendingLogRepository;
import mandarin.entities.Book;
import mandarin.entities.LendingLogItem;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class BookService {
    @Resource
    LendingLogRepository lendingLogRepository;
    @Resource
    BookRepository bookRepository;

    public boolean checkAvailibility(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new IllegalArgumentException("No such book");
        }
        return checkAvailibility(book);
    }

    public boolean checkAvailibility(Book book) {
        return lendingLogRepository.findByBookId(book.getId()).stream().noneMatch((LendingLogItem item) -> item.getEndTime() != null);
    }
}

package mandarin.dao;

import mandarin.entities.Book;
import mandarin.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findByTitleLike(String title, Pageable pageable);

    Page<Book> findByAuthorLike(String author, Pageable pageable);

    Page<Book> findByCategoriesContaining(String param, Pageable pageable);

    Page<Book> findAllByAuthorContaining(String param, Pageable pageable);

    @Query("SELECT book FROM Book book WHERE book.isbn=?1")
    Page<Book> findAllByISBN(String isbn, Pageable pageable);

    Page<Book> findAllByTitleContainsIgnoreCase(String param, Pageable pageable);
}

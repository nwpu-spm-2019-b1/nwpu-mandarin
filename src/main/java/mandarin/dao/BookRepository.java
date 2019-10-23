package mandarin.dao;

import mandarin.entities.Book;
import mandarin.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findByTitleLike(String title, Pageable pageable);

    Page<Book> findByAuthorLike(String author, Pageable pageable);

    Page<Book> findByCategoriesContaining(String param, Pageable pageable);

    Page<Book> findAllByAuthorContaining(String param, Pageable pageable);

    Page<Book> findAllByDescriptionContaining(String query, Pageable pageable);

    Page<Book> findALlByAuthorContainingIgnoreCase(String query, Pageable pageable);

    @Query("SELECT book FROM Book book WHERE book.isbn=?1")
    Page<Book> findAllByISBN(String isbn, Pageable pageable);

    Page<Book> findAllByTitleContainsIgnoreCase(String param, Pageable pageable);

    Page<Book> findAllByCategoriesContaining(Category category, Pageable pageable);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Book book WHERE book.id=?1")
    Integer deleteBookById(Integer bookId);
}

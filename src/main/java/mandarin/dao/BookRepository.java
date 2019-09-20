package mandarin.dao;

import mandarin.entities.Book;
import mandarin.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findByTitleLike(String title, Pageable pageable);
    Page<Book> findByAuthorLike(String author, Pageable pageable);
    Page<Book> findByCategoriesIsContaining(String param, Pageable pageable);
}

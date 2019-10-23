package mandarin.dao;

import mandarin.entities.NewsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<NewsItem, Integer> {
    @Query("SELECT item FROM NewsItem item ORDER BY item.time DESC")
    List<NewsItem> findAll();
}

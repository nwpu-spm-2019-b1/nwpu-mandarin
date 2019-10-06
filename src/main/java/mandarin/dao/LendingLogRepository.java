package mandarin.dao;

import mandarin.entities.Book;
import mandarin.entities.LendingLogItem;
import mandarin.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface LendingLogRepository extends JpaRepository<LendingLogItem, Integer> {

    LendingLogItem findByUserIdAndBookId(Integer userId, Integer bookId);

    Page<LendingLogItem> findByUserId(Integer userId, Pageable pageable);

    List<LendingLogItem> findByBookId(Integer bookId);

    List<LendingLogItem> findByBookId(Integer bookId, Pageable pageable);

    @Query("SELECT item FROM LendingLogItem item WHERE item.user=:user AND item.book=:book AND item.endTime=NULL")
    Optional<LendingLogItem> findOutstandingByUserAndBook(@Param("user") User user, @Param("book") Book book);

    @Query("SELECT item FROM LendingLogItem item WHERE item.book=:book AND item.endTime=NULL")
    Optional<LendingLogItem> findOutstandingByBook(@Param("book") Book book);

    @Modifying
    int deleteAllByBook(Book book);

    @Modifying
    int deleteAllByUser(User user);

}

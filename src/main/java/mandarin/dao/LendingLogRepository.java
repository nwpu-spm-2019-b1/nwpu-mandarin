package mandarin.dao;

import mandarin.entities.LendingLogItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LendingLogRepository extends JpaRepository<LendingLogItem, Integer> {

    LendingLogItem findByUserIdAndBookId(Integer userId, Integer bookId);

    Page<LendingLogItem> findByUserId(Integer userId, Pageable pageable);


    List<LendingLogItem> findByBookId(Integer bookId);

    List<LendingLogItem> findByBookId(Integer bookId, Pageable pageable);

}

package mandarin.dao;

import mandarin.entities.LendingLogItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LendingLogRepository extends JpaRepository<LendingLogItem, Integer> {

    LendingLogItem findByUserIdAndBookId(Integer userId, Integer bookId);
    Page<LendingLogItem> findByUserId(Integer userId, Pageable pageable);

    LendingLogItem findByBookId(Integer bookId);
}

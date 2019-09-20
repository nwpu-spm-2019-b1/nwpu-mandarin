package mandarin.dao;

import mandarin.entities.LendingLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LendingLogRepository extends JpaRepository<LendingLog, Integer> {

    LendingLog findByUserIdAndBookId(Integer userId, Integer bookId);
    Page<LendingLog> findByUserId(Integer userId, Pageable pageable);

}

package mandarin.dao;

import mandarin.entities.ActionLogItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLogItem, Integer> {
}

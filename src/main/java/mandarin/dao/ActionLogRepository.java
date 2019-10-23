package mandarin.dao;

import mandarin.entities.ActionLogItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ActionLogRepository extends JpaRepository<ActionLogItem, Integer> {
    List<ActionLogItem> findAllByTypeInOrderByTimeDesc(Set<String> types);
}

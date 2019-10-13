package mandarin.dao;

import mandarin.entities.FindPswordTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindPswordTableRepository extends JpaRepository<FindPswordTable, Integer>
{
    Page<FindPswordTable> findAll(Pageable pageable);
}

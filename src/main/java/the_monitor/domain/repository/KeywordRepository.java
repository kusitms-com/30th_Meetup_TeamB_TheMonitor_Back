package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import the_monitor.domain.model.Keyword;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}

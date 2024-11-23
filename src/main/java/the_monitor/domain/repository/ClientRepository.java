package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import the_monitor.domain.model.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByAccountId(@Param("accountId") Long accountId);

    List<Client> findByNameContainingIgnoreCase(String searchText);
}
package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import the_monitor.domain.model.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByAccountId(Long accountId);
}
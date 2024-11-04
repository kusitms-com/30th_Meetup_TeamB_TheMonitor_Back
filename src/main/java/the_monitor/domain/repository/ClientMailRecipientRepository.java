package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import the_monitor.domain.model.ClientMailRecipient;

public interface ClientMailRecipientRepository extends JpaRepository<ClientMailRecipient, Long> {
}

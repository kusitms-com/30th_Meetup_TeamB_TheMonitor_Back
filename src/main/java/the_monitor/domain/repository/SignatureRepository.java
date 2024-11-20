package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import the_monitor.domain.model.Signature;

public interface SignatureRepository extends JpaRepository<Signature, Long> {
}
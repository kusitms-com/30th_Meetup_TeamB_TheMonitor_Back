package the_monitor.domain.repository;

import the_monitor.domain.model.Account;
import the_monitor.infrastructure.persistence.JpaAccountRepository;

public interface AccountRepository extends JpaAccountRepository {

    Account findByEmailCertificationKey(String certifiedKey);

}

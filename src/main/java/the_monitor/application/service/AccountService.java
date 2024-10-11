package the_monitor.application.service;

import the_monitor.application.dto.request.AccountCreateRequest;
import the_monitor.domain.model.Account;

public interface AccountService {

//    void registerUser();
    Account findAccountById(Long id);

    String createAccount(AccountCreateRequest request);

}

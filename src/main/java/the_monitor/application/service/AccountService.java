package the_monitor.application.service;

import jakarta.servlet.http.HttpServletResponse;
import the_monitor.application.dto.request.AccountCreateRequest;
import the_monitor.domain.model.Account;

import java.io.IOException;

public interface AccountService {

//    void registerUser();
    Account findAccountById(Long id);

    String createAccount(AccountCreateRequest request);

    void verifyEmail(String certifiedKey, HttpServletResponse response) throws IOException;

}

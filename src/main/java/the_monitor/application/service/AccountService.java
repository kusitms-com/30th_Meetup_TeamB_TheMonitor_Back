package the_monitor.application.service;

import jakarta.servlet.http.HttpServletResponse;
import the_monitor.application.dto.request.AccountCreateRequest;
import the_monitor.application.dto.request.AccountEmailCertifyRequest;
import the_monitor.application.dto.request.AccountLoginRequest;
import the_monitor.domain.model.Account;

import java.io.IOException;

public interface AccountService {

    String sendEmailConfirm(String email);

    String verifyCode(AccountEmailCertifyRequest request);

    String createAccount(AccountCreateRequest request);

    String accountLogin(AccountLoginRequest request, HttpServletResponse response);

}

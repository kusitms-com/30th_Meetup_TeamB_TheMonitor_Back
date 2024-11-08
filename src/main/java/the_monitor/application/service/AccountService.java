package the_monitor.application.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import the_monitor.application.dto.request.*;
import the_monitor.domain.model.Account;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface AccountService {

    Account findAccountById(Long id);

    String sendEmailConfirm(AccountEmailRequest request);

    String verifyCode(AccountEmailCertifyRequest request);

    String accountSignUp(AccountSignUpRequest request);

    String accountSignIn(AccountSignInRequest request, HttpServletResponse response, HttpSession session);

    String checkEmail(String email);

    String sendPasswordChangeEmail(AccountEmailRequest request) throws UnsupportedEncodingException;

    List<Account> getAccountList();

//    String resetPassword(AccountPasswordResetRequest request) throws UnsupportedEncodingException;
}

package the_monitor.application.service;

import jakarta.servlet.http.HttpServletResponse;
import the_monitor.application.dto.request.AccountSignUpRequest;
import the_monitor.application.dto.request.AccountEmailCertifyRequest;
import the_monitor.application.dto.request.AccountSignInRequest;

public interface AccountService {

    String sendEmailConfirm(String email);

    String verifyCode(AccountEmailCertifyRequest request);

    String accountSignUp(AccountSignUpRequest request);

    String accountSignIn(AccountSignInRequest request, HttpServletResponse response);

}

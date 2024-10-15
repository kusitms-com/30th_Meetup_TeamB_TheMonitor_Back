package the_monitor.application.service;

import jakarta.servlet.http.HttpServletResponse;
import the_monitor.application.dto.request.*;

import java.io.UnsupportedEncodingException;

public interface AccountService {

    String sendEmailConfirm(AccountEmailRequest request);

    String verifyCode(AccountEmailCertifyRequest request);

    String accountSignUp(AccountSignUpRequest request);

    String accountSignIn(AccountSignInRequest request, HttpServletResponse response);

    String checkEmail(String email);

    String sendPasswordChangeEmail(AccountEmailRequest request) throws UnsupportedEncodingException;

//    String resetPassword(AccountPasswordResetRequest request) throws UnsupportedEncodingException;
}

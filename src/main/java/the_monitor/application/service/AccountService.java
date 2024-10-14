package the_monitor.application.service;

import jakarta.servlet.http.HttpServletResponse;
import the_monitor.application.dto.request.AccountPasswordResetRequest;
import the_monitor.application.dto.request.AccountSignUpRequest;
import the_monitor.application.dto.request.AccountEmailCertifyRequest;
import the_monitor.application.dto.request.AccountSignInRequest;

import java.io.UnsupportedEncodingException;

public interface AccountService {

    String sendEmailConfirm(String email);

    String verifyCode(AccountEmailCertifyRequest request);

    String accountSignUp(AccountSignUpRequest request);

    String accountSignIn(AccountSignInRequest request, HttpServletResponse response);

    String checkEmail(String email);

    String sendPasswordChangeEmail(String email) throws UnsupportedEncodingException;

//    String resetPassword(AccountPasswordResetRequest request) throws UnsupportedEncodingException;
}

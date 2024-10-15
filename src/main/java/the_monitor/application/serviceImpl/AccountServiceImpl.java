package the_monitor.application.serviceImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.request.*;
import the_monitor.application.service.AccountService;
import the_monitor.application.service.CertifiedKeyService;
import the_monitor.application.service.EmailService;
import the_monitor.application.service.TemporaryPasswordGenerateService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.model.Account;
import the_monitor.domain.repository.AccountRepository;
import the_monitor.infrastructure.jwt.JwtProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final EmailService emailService;
    private final CertifiedKeyService certifiedKeyService;
    private final TemporaryPasswordGenerateService temporaryPasswordGenerateService;

    private final JwtProvider jwtProvider;

    @Override
    public String sendEmailConfirm(AccountEmailRequest request) {

        String email = request.getEmail();

        if (accountRepository.findAccountByEmail(email) != null) throw new ApiException(ErrorStatus._ACCOUNT_ALREADY_EXIST);

        String certifiedKey = certifiedKeyService.generateCertifiedKey();

        String emailContent = "<html>" +
                "<body>" +
                "<h1>이메일 인증 요청</h1>" +
                "<p>회원가입을 위한 인증 번호를 알려드립니다.</p>" +
                "<p style=\"font-size:20px;font-weight:bold;color:#4CAF50;\">인증 번호: " + certifiedKey + "</p>" + // 인증 번호를 직접 표시
                "<p>위 인증 번호를 '이메일 인증 번호'란에 입력하고 확인 버튼을 눌러주세요.</p>" +
                "</body>" +
                "</html>";

        try {
            emailService.sendEmail(email, "The Monitor 회원가입 인증 메일입니다.", emailContent);
            log.info("이메일 전송 성공: {}", email);
            certifiedKeyService.saveCertifiedKey(email, certifiedKey);
        } catch (Exception e) {
            log.error("이메일 전송 중 오류 발생: {}", e.getMessage(), e);
            throw new ApiException(ErrorStatus._EMAIL_SEND_FAIL);
        }

        return "이메일을 발송했습니다. 인증 번호를 확인해주세요.";

    }

    @Override
    public String verifyCode(AccountEmailCertifyRequest request) {

        String storedKey = certifiedKeyService.getCertifiedKey(request.getEmail());

        if (storedKey != null && storedKey.equals(request.getVerifyCode())) {
            certifiedKeyService.deleteCertifiedKey(request.getEmail());
            return "인증이 완료되었습니다.";
        }

        else if (certifiedKeyService.isCertifiedKeyExpired(request.getEmail())) {
            return "입력 가능한 시간이 초과되었습니다.";
        } else return "인증 번호가 일치하지 않습니다.";

    }

    @Override
    @Transactional
    public String accountSignUp(AccountSignUpRequest request) {

        accountRepository.save(request.toEntity());

        return "계정 생성 완료";

    }

    @Override
    public String accountSignIn(AccountSignInRequest request, HttpServletResponse response) {

        Account account = accountRepository.findAccountByEmail(request.getEmail());

        if (account == null) throw new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND);
        if (!account.getPassword().equals(request.getPassword())) throw new ApiException(ErrorStatus._WRONG_PASSWORD);

        jwtProvider.setAddCookieToken(account, response);

        return "로그인 성공";

    }

    @Override
    public String checkEmail(String email) {

        Account account = accountRepository.findAccountByEmail(email);

        if (account == null) return "이메일이 존재하지 않습니다.";

        return "이메일이 존재합니다.";

    }

    @Override
    @Transactional
    public String sendPasswordChangeEmail(AccountEmailRequest request) {

        String email = request.getEmail();

        String temporaryPassword = temporaryPasswordGenerateService.generateTemporaryPassword();

        Account account = accountRepository.findAccountByEmail(email);
        account.resetPassword(temporaryPassword);
        accountRepository.save(account);

        String emailContent = "<!DOCTYPE html>" +
                "<html lang=\"ko\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>비밀번호 재설정</title>" +
                "<style>" +
                "body {font-family: Arial, sans-serif;}" +
                ".container {max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);}" +
                ".logo {text-align: center; margin-bottom: 20px;}" +
                ".message {font-size: 16px; margin-bottom: 30px;}" +
                ".button {display: block; width: 100%; padding: 15px; background-color: #5d5d5d; color: white; text-align: center; text-decoration: none; font-size: 18px; font-weight: bold; border-radius: 5px; margin-bottom: 20px;}" +
                ".help-text {font-size: 14px; color: #555; margin-bottom: 10px;}" +
                ".footer {font-size: 12px; color: #999; text-align: center;}" +
                ".url-link {color: #007bff; text-decoration: none;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"logo\">" +
                "<img src=\"https://your-logo-url.com/logo.png\" alt=\"The Monitor Logo\" width=\"100\">" +
                "</div>" +
                "<div class=\"message\">" +
                "안녕하세요, 더모니터입니다.<br>" +
                "<strong>" + email + "</strong> 계정의 임시 비밀번호를 보내드립니다." +
                "</div>" +
                "<strong>" + temporaryPassword + "</strong>" +
                "<div class=\"help-text\">" +
                "</div>" +
                "<div class=\"footer\">" +
                "도움이 필요하시면 <a href=\"mailto:themonitor2024@gmail.com\">themonitor2024@gmail.com</a>로 문의해주세요." +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        try {
            emailService.sendEmail(email, "The Monitor 비밀번호 재설정 요청", emailContent);
            log.info("임시 비밀번호 이메일 전송 성공: {}", email);
            log.info("임시 비밀번호: {}", account.getPassword());
        } catch (Exception e) {
            log.error("임시 비밀번호 이메일 전송 중 오류 발생: {}", e.getMessage(), e);
            throw new ApiException(ErrorStatus._EMAIL_SEND_FAIL);
        }

        return "임시 비밀번호를 발송했습니다.";

    }

//    @Override
//    public String resetPassword(AccountPasswordResetRequest request) throws UnsupportedEncodingException {
//
//        Account account = accountRepository.findAccountByEmail(request.getEmail());
//
//        if (account.getPassword().equals(request.getPassword())) throw new ApiException(ErrorStatus._SAME_PASSWORD);
//        account.resetPassword(request.getPassword());
//        accountRepository.save(account);
//
//        return "비밀번호 재설정 완료";
//
//    }

}

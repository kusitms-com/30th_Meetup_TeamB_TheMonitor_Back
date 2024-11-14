package the_monitor.application.serviceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.request.*;
import the_monitor.application.service.AccountService;
import the_monitor.application.service.CertifiedKeyService;
import the_monitor.application.service.EmailService;
import the_monitor.application.service.TemporaryPasswordGenerateService;
import the_monitor.common.ApiException;
import the_monitor.common.ApiResponse;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.model.Account;
import the_monitor.domain.repository.AccountRepository;
import the_monitor.infrastructure.jwt.JwtProvider;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final EmailService emailService;
    private final CertifiedKeyService certifiedKeyService;
    private final TemporaryPasswordGenerateService temporaryPasswordGenerateService;

    @Lazy
    private final JwtProvider jwtProvider;

    @Override
    public Account findAccountById(Long id) {

        return accountRepository.findById(id).orElseThrow(() -> new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND));

    }

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
        String email = request.getEmail();

        // 이메일 검증
        if (!certifiedKeyService.existsCertifiedKey(email)) {
            throw new ApiException(ErrorStatus._INVALID_CERTIFIED_KEY); // 이메일이 존재하지 않는 경우
        } else if (certifiedKeyService.isCertifiedKeyExpired(email)) {
            throw new ApiException(ErrorStatus._CERTIFIED_KEY_EXPIRED); // 인증번호 만료 예외
        } else if (!certifiedKeyService.getCertifiedKey(email).equals(request.getVerificationCode())) {
            throw new ApiException(ErrorStatus._INVALID_CERTIFIED_KEY); // 인증번호 불일치 예외
        }

        // 인증 성공, 인증 키 삭제
        certifiedKeyService.deleteCertifiedKey(request.getEmail());
        return "인증이 완료되었습니다."; // 인증 완료 메시지

    }

    @Override
    @Transactional
    public String accountSignUp(AccountSignUpRequest request) {

        accountRepository.save(request.toEntity());

        return "계정 생성 완료";

    }

    @Override
    public String accountSignIn(AccountSignInRequest request, HttpServletResponse response, HttpSession session) {

        Account account = accountRepository.findAccountByEmail(request.getEmail());

        if (account == null) throw new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND);
        if (!account.getPassword().equals(request.getPassword())) throw new ApiException(ErrorStatus._WRONG_PASSWORD);

        // AccessToken 발급 및 응답 헤더에 추가
        String accessToken = jwtProvider.generateAccessToken(account);
        jwtProvider.setAccessTokenInCookie(account, accessToken, response);

        // RefreshToken 발급 및 세션에 저장
        jwtProvider.storeRefreshTokenInSession(account, session);

        return "로그인 성공";

    }

    @Override
    public String checkEmail(String email) {

        Account account = accountRepository.findAccountByEmail(email);

        if (account == null) throw new ApiException(ErrorStatus._ACCOUNT_NOT_EXIST);

        return "이메일이 존재합니다.";

    }

    @Override
    @Transactional
    public String sendPasswordChangeEmail(AccountEmailRequest request) {

        String email = request.getEmail();

        Account account = accountRepository.findAccountByEmail(email);

        if (account == null) {
            throw new ApiException(ErrorStatus._ACCOUNT_NOT_EXIST);
        }

        String temporaryPassword = temporaryPasswordGenerateService.generateTemporaryPassword();

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

    @Override
    public List<Account> getAccountList() {

        return accountRepository.findAll();

    }

    @Override
    public ApiResponse<String> checkTokenValidity(HttpServletRequest request, HttpServletResponse response) {
        // SecurityContextHolder에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.onFailure("401", "토큰을 찾을 수 없습니다", null);
        }

        // 인증된 토큰의 유효성 검사
        String token = (String) authentication.getCredentials();
        String tokenStatus = jwtProvider.validateToken(token);

        if ("VALID".equals(tokenStatus)) {
            return ApiResponse.onSuccess("Token is valid");
        } else if ("EXPIRED".equals(tokenStatus)) {
            // 리프레시 토큰으로 새로운 액세스 토큰 발급
            String refreshToken = (String) request.getSession(false).getAttribute("refreshToken");

            if (refreshToken != null && "VALID".equals(jwtProvider.validateToken(refreshToken))) {
                Long accountId = jwtProvider.getAccountId(refreshToken);
                Account account = accountRepository.findById(accountId)
                        .orElseThrow(() -> new ApiException(ErrorStatus._ACCOUNT_NOT_EXIST));

                String newAccessToken = jwtProvider.generateAccessToken(account);
                jwtProvider.setAccessTokenInCookie(account, newAccessToken, response);
                return ApiResponse.onSuccess("Access token refreshed");
            } else {
                return ApiResponse.onFailure("401", "리프레시 토큰이 만료되었습니다.", null);
            }
        } else {
            return ApiResponse.onFailure("401", "유효하지 않은 토큰입니다", null);
        }
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

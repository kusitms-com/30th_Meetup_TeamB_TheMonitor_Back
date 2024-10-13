package the_monitor.application.serviceImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.request.AccountCreateRequest;
import the_monitor.application.dto.request.AccountLoginRequest;
import the_monitor.application.service.AccountService;
import the_monitor.application.service.EmailService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.model.Account;
import the_monitor.domain.repository.AccountRepository;
import the_monitor.infrastructure.jwt.JwtProvider;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;

    @Override
    public Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND));
    }

    @Override
    @Transactional
    public String createAccount(AccountCreateRequest request) {

        String certifiedKey = generateCertifiedKey();
        accountRepository.save(request.toEntity(certifiedKey));
        log.info("계정 생성 완료. 이메일 발송 중...");

        String verificationLink = "http://localhost:8080/api/v1/account/verify?certifiedKey=" + certifiedKey; // 이거 나중에 프론트 URL로 변경 필요
        // 아래도 마찬가지로 링크 변경 필요.
        String emailContent = "<html>" +
                "<body>" +
                "<h1>이메일 인증 요청</h1>" +
                "<p>회원가입을 완료하려면 아래 버튼을 클릭하여 이메일 인증을 완료해 주세요.</p>" +
                "<a href=\"" + verificationLink + "\" style=\"display:inline-block;padding:10px 20px;background-color:#4CAF50;color:white;text-decoration:none;border-radius:5px;\">이메일 인증하기</a>" +
                "<p>위 버튼을 클릭할 수 없다면 아래 링크를 복사하여 브라우저에 붙여넣어 주세요:</p>" +
                "<p><a href=\"" + verificationLink + "\">" + verificationLink + "</a></p>" +
                "</body>" +
                "</html>";
        try {
            emailService.sendEmail(request.getEmail(), "The Monitor 회원가입 인증 메일입니다.", emailContent);
        } catch (Exception e) {
            throw new ApiException(ErrorStatus._EMAIL_SEND_FAIL);
        }

        return "계정 생성 완료. 이메일을 발송했습니다. 인증을 완료해주세요.";

    }

    @Override
    public void verifyEmail(String certifiedKey, HttpServletResponse response) throws IOException {

        Account account = accountRepository.findByEmailCertificationKey(certifiedKey);
        if (account == null) throw new ApiException(ErrorStatus._INVALID_CERTIFIED_KEY);

        account.setEmailVerified();

        // 인증 성공 후 리다이렉트할 URL
        String redirectUrl = "http://localhost:3000/email-verification-success"; // 리다이렉트할 URL 이후 수정 필요
        response.sendRedirect(redirectUrl);  // 사용자를 해당 URL로 리다이렉트

    }

    @Override
    public String accountLogin(AccountLoginRequest request, HttpServletResponse response) {

        Account account = accountRepository.findByEmail(request.getEmail());

        if (account == null) throw new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND);

        if (!account.getPassword().equals(request.getPassword())) throw new ApiException(ErrorStatus._WRONG_PASSWORD);

        if (account.isEmailVerified()) {

            jwtProvider.setAddCookieToken(account, response);

            return "로그인 성공";
        }

        return "이메일 인증 필요";
    }

    private String generateCertifiedKey() {
        return UUID.randomUUID().toString();  // UUID 생성
    }

}

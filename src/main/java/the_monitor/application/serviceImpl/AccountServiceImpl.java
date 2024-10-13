package the_monitor.application.serviceImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.request.AccountCreateRequest;
import the_monitor.application.dto.request.AccountEmailCertifyRequest;
import the_monitor.application.dto.request.AccountLoginRequest;
import the_monitor.application.service.AccountService;
import the_monitor.application.service.CertifiedKeyService;
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
    private final CertifiedKeyService certifiedKeyService;
    private final JwtProvider jwtProvider;

    @Override
    public String sendEmailConfirm(String email) {

        String certifiedKey = certifiedKeyService.generateCertifiedKey();

        String emailContent = "<html>" +
                "<body>" +
                "<h1>이메일 인증 요청</h1>" +
                "<p>회원가입을 위한 인증 번호를 알려드립니다.</p>" +
                "<p style=\"font-size:20px;font-weight:bold;color:#4CAF50;\">인증 번호: " + certifiedKey + "</p>" + // 인증 번호를 직접 표시
                "<p>위 인증 번호를 '이메일 인증 번호'란에 입력하고 확인 버튼을 눌러주세요.</p>" +
                "</body>" +
                "</html>";

//        try {
//            emailService.sendEmail(email, "The Monitor 회원가입 인증 메일입니다.", emailContent);
//            certifiedKeyService.saveCertifiedKey(email, certifiedKey);
//        } catch (Exception e) {
//            throw new ApiException(ErrorStatus._EMAIL_SEND_FAIL);
//        }
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
            return "인증 성공";
        }

        else return "인증 실패";

    }

    @Override
    @Transactional
    public String createAccount(AccountCreateRequest request) {

        accountRepository.save(request.toEntity());

        return "계정 생성 완료";

    }

    @Override
    public String accountLogin(AccountLoginRequest request, HttpServletResponse response) {

//        Account account = accountRepository.findByEmail(request.getEmail());
//
//        if (account == null) throw new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND);
//
//        if (!account.getPassword().equals(request.getPassword())) throw new ApiException(ErrorStatus._WRONG_PASSWORD);
//
//        if (account.isEmailVerified()) {
//
//            jwtProvider.setAddCookieToken(account, response);
//
//            return "로그인 성공";
//        }

        return "이메일 인증 필요";
    }


}

package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.AccountPasswordResetRequest;
import the_monitor.application.dto.request.AccountSignUpRequest;
import the_monitor.application.dto.request.AccountEmailCertifyRequest;
import the_monitor.application.dto.request.AccountSignInRequest;
import the_monitor.application.service.AccountService;
import the_monitor.common.ApiResponse;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "이메일 인증 요청", description = "이메일 인증 요청을 보냅니다.")
    @GetMapping("/sendEmailConfirm")
    public ApiResponse<String> sendEmailConfirm(@RequestParam("email") String email) {

        return ApiResponse.onSuccess(accountService.sendEmailConfirm(email));

    }

    @Operation(summary = "인증코드 확인", description = "인증코드를 확인합니다.")
    @PostMapping("/verifyCode")
    public ApiResponse<String> verifyCode(@RequestBody @Valid AccountEmailCertifyRequest request) {

        return ApiResponse.onSuccess(accountService.verifyCode(request));

    }

    @Operation(summary = "계정 생성", description = "회원가입을 진행합니다.")
    @PostMapping("/signUp")
    public ApiResponse<String> createAccount(@RequestBody @Valid AccountSignUpRequest request){

        return ApiResponse.onSuccess(accountService.accountSignUp(request));

    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/signIn")
    public ApiResponse<String> Login(@RequestBody @Valid AccountSignInRequest request, HttpServletResponse response) {

        return ApiResponse.onSuccess(accountService.accountSignIn(request, response));

    }

    @Operation(summary = "이메일 존재 확인", description = "비밀번호 변경 전, 이메일 존재 여부를 확인합니다.")
    @GetMapping("/checkEmail")
    public ApiResponse<String> checkEmail(@RequestParam("email") String email) {

        return ApiResponse.onSuccess(accountService.checkEmail(email));

    }

    @Operation(summary = "비밀번호 변경 메일 발송", description = "비밀번호 변경 메일을 발송합니다.")
    @GetMapping("/sendPasswordChangeEmail")
    public ApiResponse<String> sendPasswordChangeEmail(@RequestParam("email") String email) throws UnsupportedEncodingException {

        return ApiResponse.onSuccess(accountService.sendPasswordChangeEmail(email));

    }

    @Operation(summary = "비밀번호 재설정", description = "비밀번호를 재설정합니다.")
    @PostMapping("/resetPassword")
    public ApiResponse<String> resetPassword(@RequestBody @Valid AccountPasswordResetRequest request) throws UnsupportedEncodingException {

        return ApiResponse.onSuccess(accountService.resetPassword(request));

    }

}

package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.AccountCreateRequest;
import the_monitor.application.dto.request.AccountEmailCertifyRequest;
import the_monitor.application.dto.request.AccountLoginRequest;
import the_monitor.application.service.AccountService;
import the_monitor.common.ApiResponse;

import java.io.IOException;

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
    public ApiResponse<String> createAccount(@RequestBody @Valid AccountCreateRequest request){

        return ApiResponse.onSuccess(accountService.createAccount(request));

    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/signIn")
    public ApiResponse<String> Login(@RequestBody @Valid AccountLoginRequest request, HttpServletResponse response) {

        return ApiResponse.onSuccess(accountService.accountLogin(request, response));

    }

}

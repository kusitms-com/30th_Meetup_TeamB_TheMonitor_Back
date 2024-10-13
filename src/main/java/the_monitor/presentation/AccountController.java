package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.AccountCreateRequest;
import the_monitor.application.dto.request.AccountLoginRequest;
import the_monitor.application.service.AccountService;
import the_monitor.common.ApiResponse;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "계정 생성", description = "회원가입을 진행합니다.")
    @PostMapping("/createAccount")
    public ApiResponse<String> createAccount(@RequestBody @Valid AccountCreateRequest request){

        return ApiResponse.onSuccess(accountService.createAccount(request));

    }

    @Operation(summary = "이메일 인증", description = "이메일 인증을 완료합니다.")
    @GetMapping("/verify")
    public void verifyEmail(@RequestParam("certifiedKey") String certifiedKey, HttpServletResponse response) throws IOException {

        accountService.verifyEmail(certifiedKey, response);

    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @GetMapping("/login")
    public ApiResponse<String> Login(@RequestBody @Valid AccountLoginRequest request, HttpServletResponse response) {

        return ApiResponse.onSuccess(accountService.accountLogin(request, response));

    }

}

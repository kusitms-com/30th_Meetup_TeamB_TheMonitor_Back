package the_monitor.presentation;

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

    @PostMapping("/createAccount")
    public ApiResponse<String> createAccount(@RequestBody @Valid AccountCreateRequest request){

        return ApiResponse.onSuccess(accountService.createAccount(request));

    }

    @GetMapping("/verify")
    public void verifyEmail(@RequestParam("certifiedKey") String certifiedKey, HttpServletResponse response) throws IOException {

        accountService.verifyEmail(certifiedKey, response);

    }

    @GetMapping("/login")
    public ApiResponse<String> Login(@RequestBody @Valid AccountLoginRequest request, HttpServletResponse response) {

        return ApiResponse.onSuccess(accountService.accountLogin(request, response));

    }

}

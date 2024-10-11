package the_monitor.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import the_monitor.application.dto.request.AccountCreateRequest;
import the_monitor.application.service.AccountService;
import the_monitor.common.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/createAccount")
    public ApiResponse<String> createAccount(@RequestBody @Valid AccountCreateRequest request){

        return ApiResponse.onSuccess(accountService.createAccount(request));

    }



}

package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.EmailUpdateRequest;
import the_monitor.application.dto.response.EmailResponse;
import the_monitor.application.service.EmailService;
import the_monitor.common.ApiResponse;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @Operation(summary = "이메일 조회", description = "clientId에 따른 이메일 리스트를 조회합니다.")
    @GetMapping
    public ApiResponse<EmailResponse> getEmails(@RequestParam("clientId") Long clientId) {
        EmailResponse emailResponse = emailService.getEmails(clientId);
        return ApiResponse.onSuccessData("이메일 조회 성공", emailResponse);
    }

    @Operation(summary = "이메일 수정", description = "clientId에 따른 이메일 리스트를 수정합니다.")
    @PutMapping
    public ApiResponse<EmailResponse> updateEmails(@RequestParam("clientId") Long clientId, @RequestBody EmailUpdateRequest emailUpdateRequest) {
        EmailResponse updatedEmails = emailService.updateEmails(clientId, emailUpdateRequest);
        return ApiResponse.onSuccessData("이메일 수정 성공", updatedEmails);
    }

}

package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import the_monitor.application.dto.response.KeywordResponse;
import the_monitor.application.service.KeywordService;
import the_monitor.common.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/keywords")
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "키워드 리스트 반환", description = "clientId에 따른 키워드를 반환합니다.")
    @GetMapping()
    public ApiResponse<KeywordResponse> getKeywords(@RequestParam("clientId") Long clientId) {
        return ApiResponse.onSuccessData("keyword 리스트", keywordService.getKeywords(clientId));
    }

}

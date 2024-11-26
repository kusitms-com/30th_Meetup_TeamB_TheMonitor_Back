package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.ScrapArticleDto;
import the_monitor.application.dto.request.ScrapIdsRequest;
import the_monitor.application.dto.request.ScrapReportArticleRequest;
import the_monitor.application.dto.response.ScrapArticleListResponse;
import the_monitor.application.dto.response.ScrapReportArticeResponse;
import the_monitor.application.service.ScrapService;
import the_monitor.common.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scrap")
public class ScrapController {

    private final ScrapService scrapService;

    @Operation(summary = "스크랩하기", description = "기사를 스크랩하여 scrapId를 반환합니다.")
    @PostMapping()
    public ApiResponse<ScrapReportArticeResponse> scrapArticle(@RequestParam("clientId") Long clientId,
                                                               @RequestBody ScrapReportArticleRequest request) {

        return ApiResponse.onSuccessData("기사 스크랩 성공", scrapService.scrapArticle(clientId, request));

    }

    @Operation(summary = "스크랩한 기사 조회", description = "스크랩한 기사들을 조회합니다.")
    @GetMapping
    public ApiResponse<List<ScrapArticleDto>> getScrapedArticlesByClientId(@RequestParam("clientId") Long clientId) {
        return ApiResponse.onSuccessData("스크랩 기사 조회 성공", scrapService.getScrapedArticlesByClientId(clientId));
    }

}

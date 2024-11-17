package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.ScrapReportArticleRequest;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.dto.response.ScrapReportArticeResponse;
import the_monitor.application.service.ArticleService;
import the_monitor.common.ApiResponse;
import the_monitor.common.PageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "default 기사 출력", description = "기본으로 보여지는 기사들을 출력합니다.")
    @GetMapping()
    public ApiResponse<PageResponse<ArticleResponse>> getArticles(@RequestParam("clientId") Long clientId,
                                                                  @RequestParam("page") int page) {

        return ApiResponse.onSuccessData("default 기사", articleService.getDefaultArticles(clientId, page));

    }

    @Operation(summary = "keyword 기사 출력", description = "keyword에 해당하는 기사를 출력합니다.")
    @GetMapping("/search")
    public ApiResponse<PageResponse<ArticleResponse>> getArticlesBySearch(@RequestParam("keyword") String keyword,
                                                                          @RequestParam("page") int page) {

        return ApiResponse.onSuccessData("검색 기사", articleService.getArticlesBySearch(keyword, page));

    }

}

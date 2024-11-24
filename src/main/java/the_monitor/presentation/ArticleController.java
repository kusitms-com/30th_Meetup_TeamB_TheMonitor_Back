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
import the_monitor.domain.enums.CategoryType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "기사 저장", description = "키워드에 해당하는 기사를 저장합니다.")
    @PostMapping()
    public ApiResponse<String> saveArticles(Long clientId) {

        return ApiResponse.onSuccess(articleService.saveArticles(clientId));

    }

    @Operation(summary = "전체 기사 출력", description = "카테고리별로 모든 키워드의 기사들을 출력합니다.")
    @GetMapping()
    public ApiResponse<PageResponse<ArticleResponse>> getArticles(@RequestParam("clientId") Long clientId,
                                                                  @RequestParam("categoryType") CategoryType categoryType,
                                                                  @RequestParam("page") int page) {

        return ApiResponse.onSuccessData(categoryType + " 기사", articleService.getArticlesGroupByCategory(clientId, categoryType, page));

    }

    @Operation(summary = "keyword 기사 출력", description = "keyword에 해당하는 기사를 출력합니다.")
    @GetMapping("/search")
    public ApiResponse<PageResponse<ArticleResponse>> getArticlesBySearch(@RequestParam("keyword") String keyword,
                                                                          @RequestParam("page") int page) {

        return ApiResponse.onSuccessData("검색 기사", articleService.getArticlesBySearch(keyword, page));

    }

}

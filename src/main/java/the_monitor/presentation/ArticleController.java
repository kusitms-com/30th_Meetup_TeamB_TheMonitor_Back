package the_monitor.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.ArticleRequest;
import the_monitor.application.dto.request.ArticleSearchRequest;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.service.ArticleService;
import the_monitor.common.ApiResponse;
import the_monitor.common.PageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping()
    public ApiResponse<PageResponse<ArticleResponse>> getArticles(@RequestParam("page") int page) {

        return ApiResponse.onSuccessData("default 기사", articleService.getDefaultArticles(page));

    }

    @PostMapping("/search")
    public ApiResponse<PageResponse<ArticleResponse>> getArticlesBySearch(@RequestParam("keyword") String keyword,
                                                                          @RequestParam("categoryId") Long categoryId,
                                                                          @RequestParam("page") int page) {

        return ApiResponse.onSuccessData("검색 기사", articleService.getArticlesBySearch("w1", keyword, categoryId, page));

    }

}

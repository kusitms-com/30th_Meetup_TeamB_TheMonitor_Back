package the_monitor.application.service;

import the_monitor.application.dto.request.ScrapReportArticleRequest;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.dto.response.ScrapReportArticeResponse;
import the_monitor.common.PageResponse;
import the_monitor.domain.enums.CategoryType;

public interface ArticleService {

    PageResponse<ArticleResponse> getArticlesGroupByCategory(Long clientId, CategoryType categoryType, int page);

    PageResponse<ArticleResponse> getArticlesBySearch(String keyword, int page);

}

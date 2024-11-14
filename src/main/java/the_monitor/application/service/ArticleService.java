package the_monitor.application.service;

import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.common.PageResponse;

public interface ArticleService {

    PageResponse<ArticleResponse> getDefaultArticles(Long clientId, int page);

    PageResponse<ArticleResponse> getArticlesBySearch(String keyword, int page);


}

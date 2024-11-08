package the_monitor.application.service;

import the_monitor.application.dto.request.ArticleRequest;
import the_monitor.application.dto.request.ArticleSearchRequest;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.common.PageResponse;

public interface ArticleService {

    PageResponse<ArticleResponse> getDefaultArticles(String token, ArticleRequest request);

    PageResponse<ArticleResponse> getArticlesBySearch(String token, ArticleSearchRequest request);



}

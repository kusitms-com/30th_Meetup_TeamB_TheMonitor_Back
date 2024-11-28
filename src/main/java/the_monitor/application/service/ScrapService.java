package the_monitor.application.service;

import the_monitor.application.dto.ScrapArticleDto;
import the_monitor.application.dto.response.ScrapCategoryTypeResponse;
import the_monitor.domain.model.Scrap;

import java.util.List;

public interface ScrapService {

    Scrap findById(Long scrapId);

    String scrapArticle(Long articleId);

//    ScrapArticleListResponse getScrapArticleList(Long clientId, ScrapIdsRequest request);

    ScrapCategoryTypeResponse getScrappedArticlesByClientId();

}

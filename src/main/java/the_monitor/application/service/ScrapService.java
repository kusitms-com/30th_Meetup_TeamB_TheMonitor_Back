package the_monitor.application.service;

import the_monitor.application.dto.ScrapArticleDto;
import the_monitor.application.dto.response.ScrapCategoryTypeResponse;
public interface ScrapService {

    String scrapArticle(Long articleId);

    ScrapCategoryTypeResponse getScrappedArticlesByClientId();

    ScrapArticleDto getScrapArticleInfo(Long scrapId);

    String unScrapArticle();

}

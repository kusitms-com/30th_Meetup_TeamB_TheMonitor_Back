package the_monitor.application.service;

import the_monitor.application.dto.ScrapArticleDto;
import the_monitor.application.dto.request.ScrapIdsRequest;
import the_monitor.application.dto.request.ScrapReportArticleRequest;
import the_monitor.application.dto.response.ScrapArticleListResponse;
import the_monitor.application.dto.response.ScrapReportArticeResponse;
import the_monitor.domain.model.Scrap;

import java.util.List;

public interface ScrapService {

    Scrap findById(Long scrapId);

    ScrapReportArticeResponse scrapArticle(Long articleId);

//    ScrapArticleListResponse getScrapArticleList(Long clientId, ScrapIdsRequest request);

    List<ScrapArticleDto> getScrapedArticlesByClientId();

    String unscrapArticle(Long articleId);

}

package the_monitor.application.service;

import the_monitor.application.dto.ArticleGoogleDto;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.domain.model.Keyword;

import java.util.List;

public interface GoogleSearchService {

    ArticleResponse toDto(String keyword);

    ArticleResponse searchArticlesWithoutSaving(String keyword, int page, int size);

}

package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.application.dto.ArticleGoogleDto;
import the_monitor.application.dto.ReportArticleDto;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Scrap;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ScrapReportArticleRequest {

    private Map<CategoryType, ArticleGoogleDto> reportArticles;

    @Builder
    public ScrapReportArticleRequest(Map<CategoryType, ArticleGoogleDto> reportArticles) {
        this.reportArticles = reportArticles;
    }

}

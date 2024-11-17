package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.application.dto.ReportArticleDto;
import the_monitor.domain.enums.CategoryType;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ScrapArticleListResponse {

    private Map<CategoryType, List<ReportArticleDto>> reportArticles;

    @Builder
    public ScrapArticleListResponse(Map<CategoryType, List<ReportArticleDto>> reportArticles) {
        this.reportArticles = reportArticles;
    }

}

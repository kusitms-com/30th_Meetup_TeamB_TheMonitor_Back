package the_monitor.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ReportCategoryArticleDto {

    // 카테고리별 기사 리스트
    private Map<String, List<ReportArticleDto>> reportArticles;

    @Builder
    public ReportCategoryArticleDto(Map<String, List<ReportArticleDto>> reportArticles) {
        this.reportArticles= reportArticles;
    }

}

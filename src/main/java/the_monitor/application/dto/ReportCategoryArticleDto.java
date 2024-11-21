package the_monitor.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ReportCategoryArticleDto {

    private CategoryType categoryType;

    // 카테고리별 기사 리스트
    private List<ReportArticleDto> reportArticles;

    @Builder
    public ReportCategoryArticleDto(CategoryType categoryType,
                                    List<ReportArticleDto> reportArticles) {

        this.categoryType= categoryType;
        this.reportArticles= reportArticles;

    }

}

package the_monitor.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Report;
import the_monitor.domain.model.ReportArticle;
import the_monitor.domain.model.ReportCategory;

@Getter
@NoArgsConstructor
public class ReportArticleUpdateRequest {

    @NotBlank(message = "카테고리는 필수입니다.")
    private String categoryType;

    @NotBlank(message = "키워드는 필수입니다.")
    private String category;

    @NotBlank(message = "기사 제목은 필수입니다.")
    private String articleTitle;

    @NotBlank(message = "url은 필수입니다.")
    private String url;

    @NotBlank(message = "발행일자는 필수입니다.")
    private String publishedDate;

    @NotBlank(message = "미디어는 필수입니다.")
    private String pulisherName;

    @NotBlank(message = "기자명은 필수입니다.")
    private String reporterName;

    @NotBlank(message = "카테고리ID는 필수입니다.")
    private Long reportCategoryId;

    @Builder
    public ReportArticleUpdateRequest(String categoryType,
                                      String category,
                                      String articleTitle,
                                      String url,
                                      String publishedDate,
                                      String pulisherName,
                                      String reporterName,
                                      Long reportCategoryId) {

        this.categoryType = categoryType;
        this.category = category;
        this.articleTitle = articleTitle;
        this.url = url;
        this.publishedDate = publishedDate;
        this.pulisherName = pulisherName;
        this.reporterName = reporterName;

    }

    public ReportArticle toEntity(ReportCategory reportCategory) {
        return ReportArticle.builder()
                .categoryType(CategoryType.valueOf(categoryType))
                .title(articleTitle)
                .url(url)
                .publishDate(publishedDate)
                .publisherName(pulisherName)
                .reporterName(reporterName)
                .reportCategory(reportCategory)
                .build();
    }

}

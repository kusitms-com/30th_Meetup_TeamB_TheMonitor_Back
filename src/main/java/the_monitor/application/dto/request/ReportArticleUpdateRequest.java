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
    private String keyword;

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


    @Builder
    public ReportArticleUpdateRequest(String categoryType,
                                      String keyword,
                                      String articleTitle,
                                      String url,
                                      String publishedDate,
                                      String pulisherName,
                                      String reporterName) {

        this.categoryType = categoryType;
        this.keyword = keyword;
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
                .keyword(keyword)
                .url(url)
                .publishDate(publishedDate)
                .publisherName(pulisherName)
                .reporterName(reporterName)
                .reportCategory(reportCategory)
                .build();
    }

}

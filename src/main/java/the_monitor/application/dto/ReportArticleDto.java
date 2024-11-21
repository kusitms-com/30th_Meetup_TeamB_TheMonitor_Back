package the_monitor.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.model.Report;
import the_monitor.domain.model.ReportArticle;

@Getter
@NoArgsConstructor
public class ReportArticleDto {

    private String keyword;
    private String publishedDate;
    private String headLine;
    private String url;
    private String media;
    private String reporter;
    private String summary;

    @Builder
    public ReportArticleDto(String publishedDate,
                            String keyword,
                            String headLine,
                            String url,
                            String media,
                            String reporter,
                            String summary) {

        this.publishedDate = publishedDate;
        this.keyword = keyword;
        this.headLine = headLine;
        this.url = url;
        this.media = media;
        this.reporter = reporter;
        this.summary = summary;

    }

    public ReportArticle toEntity(Report report) {
        return ReportArticle.builder()
                .report(report)
                .publishDate(publishedDate)
                .category(keyword)
                .title(headLine)
                .url(url)
                .publisherName(media)
                .reporterName(reporter)
                .summary(summary)
                .build();
    }

}

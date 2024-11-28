package the_monitor.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.model.Article;
import the_monitor.domain.model.Keyword;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ArticleGoogleDto {

    private Long articleId;
    private String title;
    private String body;
    private String url;
    private String imageUrl;
    private String publisherName;
    private String publishDate;
    private String reporterName;
    private boolean scrapped = false;

    @Builder
    public ArticleGoogleDto(Long articleId,
                            String title,
                            String body,
                            String url,
                            String imageUrl,
                            String publisherName,
                            String publishDate,
                            String reporterName,
                            boolean scrapped) {

        this.articleId = articleId;
        this.title = title;
        this.body = body;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publisherName = publisherName;
        this.publishDate = publishDate;
        this.reporterName = reporterName;
        this.scrapped = scrapped;

    }

    public Article toEntity(Keyword keyword) {
        return Article.builder()
                .title(title)
                .body(body)
                .url(url)
                .imageUrl(imageUrl)
                .publisherName(publisherName)
                .publishDate(publishDate)
                .reporterName(reporterName)
                .isScrapped(scrapped)
                .keyword(keyword)
                .build();
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }


}
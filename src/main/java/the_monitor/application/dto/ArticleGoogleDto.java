package the_monitor.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.model.Article;
import the_monitor.domain.model.Keyword;

@Getter
@NoArgsConstructor
public class ArticleGoogleDto {

    private String title;
    private String body;
    private String url;
    private String imageUrl;
    private String publisherName;
    private String publishDate;
    private String reporterName;
    private boolean portalGoogle;

    @Builder
    public ArticleGoogleDto(String title,
                            String body,
                            String url,
                            String imageUrl,
                            String publisherName,
                            String publishDate,
                            String reporterName) {

        this.title = title;
        this.body = body;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publisherName = publisherName;
        this.publishDate = publishDate;
        this.reporterName = reporterName;
        this.portalGoogle = true;

    }

    // Article 엔티티로 변환하는 메서드
    public Article toEntity(Keyword keyword) {

        return Article.builder()
                .keyword(keyword)
                .title(title)
                .body(body)
                .url(url)
                .image(imageUrl)
                .publisherName(publisherName)
                .publishDate(publishDate)
                .reporterName(reporterName)
                .portalGoogle(portalGoogle)
                .build();

    }

}
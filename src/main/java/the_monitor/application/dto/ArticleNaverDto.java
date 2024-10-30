package the_monitor.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.model.Article;

@Getter
@NoArgsConstructor
public class ArticleNaverDto {

    private String title;
    private String body;
    private String url;
    private String publishDate;
    private boolean portalNaver;

    @Builder
    public ArticleNaverDto(String title, String body, String url, String publishDate) {
        this.title = title;
        this.body = body;
        this.url = url;
        this.publishDate = publishDate;
        this.portalNaver = true;
    }

    public Article toEntity() {
        return Article.builder()
                .title(title)
                .body(body)
                .url(url)
                .publishDate(publishDate)
                .portalNaver(portalNaver)
                .build();
    }

}

package the_monitor.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    }

}
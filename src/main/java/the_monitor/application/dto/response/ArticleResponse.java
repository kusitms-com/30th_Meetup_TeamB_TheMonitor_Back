package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArticleResponse {

    private String title;
    private String body;
    private String url;
    private String image;
    private String publisherName;
    private String reporterName;
    private String publishDate;

    @Builder
    public ArticleResponse(String title,
                           String body,
                           String url,
                           String image,
                           String publisherName,
                           String reporterName,
                           String publishDate) {

        this.title = title;
        this.body = body;
        this.url = url;
        this.image = image;
        this.publisherName = publisherName;
        this.reporterName = reporterName;
        this.publishDate = publishDate;

    }

}

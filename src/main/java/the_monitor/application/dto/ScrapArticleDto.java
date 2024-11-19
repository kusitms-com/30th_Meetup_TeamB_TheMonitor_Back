package the_monitor.application.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;

@Getter
@NoArgsConstructor
public class ScrapArticleDto {

    private String title;
    private String body;
    private String url;
    private String imageUrl;
    private String publisherName;
    private String publishDate;
    private String reporterName;
    private CategoryType categoryType;

    @Builder
    public ScrapArticleDto(String title,
                           String body,
                           String url,
                           String imageUrl,
                           String publisherName,
                           String publishDate,
                           String reporterName,
                           CategoryType categoryType) {

        this.title = title;
        this.body = body;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publisherName = publisherName;
        this.publishDate = publishDate;
        this.reporterName = reporterName;
        this.categoryType = categoryType;

    }


}

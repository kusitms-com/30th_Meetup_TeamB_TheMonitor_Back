package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.application.dto.ArticleGoogleDto;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleResponse {

    private List<ArticleGoogleDto> googleArticles;
    private int totalResults;

    @Builder
    public ArticleResponse(List<ArticleGoogleDto> googleArticles, int totalResults) {
        this.googleArticles = googleArticles;
        this.totalResults = totalResults;
    }

}

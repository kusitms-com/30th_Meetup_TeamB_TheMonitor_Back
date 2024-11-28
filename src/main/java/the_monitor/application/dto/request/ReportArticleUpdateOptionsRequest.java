package the_monitor.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportArticleUpdateOptionsRequest {

    @JsonProperty("isMedia")
    private boolean isMedia;
    @JsonProperty("isReporter")
    private boolean isReporter;

    @Builder
    public ReportArticleUpdateOptionsRequest(boolean isMedia,
                                             boolean isReporter) {
        this.isMedia = isMedia;
        this.isReporter = isReporter;

    }

}

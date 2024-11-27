package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportOptionsResponse {

    @JsonProperty("isMedia")
    private boolean isMedia;
    @JsonProperty("isReporter")
    private boolean isReporter;

    @Builder
    public ReportOptionsResponse(boolean isMedia,
                                 boolean isReporter) {

        this.isMedia = isMedia;
        this.isReporter = isReporter;

    }

}

package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportOptionsResponse {

    private boolean isMedia;
    private boolean isReporter;

    @Builder
    public ReportOptionsResponse(boolean isMedia,
                                 boolean isReporter) {

        this.isMedia = isMedia;
        this.isReporter = isReporter;

    }

}

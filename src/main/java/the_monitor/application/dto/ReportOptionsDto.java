package the_monitor.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportOptionsDto {

    private boolean isMedia;
    private boolean isReporter;

    @Builder
    public ReportOptionsDto(boolean isMedia,
                            boolean isReporter) {

        this.isMedia = isMedia;
        this.isReporter = isReporter;

    }

}

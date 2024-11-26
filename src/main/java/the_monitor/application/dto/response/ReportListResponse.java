package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReportListResponse {

    private Long reportId;
    private String title;

    @Builder
    public ReportListResponse(Long reportId, String title) {

        this.reportId = reportId;
        this.title = title;

    }

}

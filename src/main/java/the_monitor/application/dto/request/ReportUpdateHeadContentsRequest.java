package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportUpdateHeadContentsRequest {

    private String title;
    private String color;

    @Builder
    public ReportUpdateHeadContentsRequest(String title, String color) {
        this.title = title;
        this.color = color;
    }

}

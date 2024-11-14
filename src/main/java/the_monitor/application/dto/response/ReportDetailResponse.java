package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Category;
import the_monitor.domain.model.ReportArticle;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ReportDetailResponse {

    private Long reportId;
    private String title;
    private String logo;
    private String color;

    @Builder
    public ReportDetailResponse(Long reportId,
                                String title,
                                String logo,
                                String color) {

        this.reportId = reportId;
        this.title = title;
        this.logo = logo;
        this.color = color;

    }

}

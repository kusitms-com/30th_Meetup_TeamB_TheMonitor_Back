package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReportCategoryResponse {

    private Long reportCategoryId;
    private String reportCategoryName;
    private String reportCategoryDescription;

    private List<ReportArticlesResponse> reportArticlesResponses = new ArrayList<>();

    @Builder
    public ReportCategoryResponse(Long reportCategoryId,
                                  String reportCategoryName,
                                  String reportCategoryDescription,
                                  List<ReportArticlesResponse> reportArticlesResponses) {

        this.reportCategoryId = reportCategoryId;
        this.reportCategoryName = reportCategoryName;
        this.reportCategoryDescription = reportCategoryDescription;
        this.reportArticlesResponses = reportArticlesResponses;

    }

}

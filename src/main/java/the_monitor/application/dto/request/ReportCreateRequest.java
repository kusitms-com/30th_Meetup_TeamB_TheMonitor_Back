package the_monitor.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import the_monitor.application.dto.ReportCategoryArticleDto;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.Report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ReportCreateRequest {

    private String reportTitle;
    private String color;

    // 유형(자사, 경쟁사, 고객사)별 카테고리 - 기사 리스트
    private List<ReportCategoryArticleDto> reportArticles;

    @Builder
    public ReportCreateRequest(String reportTitle,
                               String color,
                               List<ReportCategoryArticleDto> reportArticles) {

        this.reportTitle = reportTitle;
        this.color = color;
        this.reportArticles = reportArticles;

    }

    public Report toEntity(Client client, String logoUrl) {
        return Report.builder()
                .client(client)
                .title(reportTitle)
                .color(color)
                .logo(logoUrl)
                .build();
    }

}

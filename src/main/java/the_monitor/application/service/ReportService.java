package the_monitor.application.service;

import the_monitor.application.dto.request.ReportArticleUpdateRequest;
import the_monitor.application.dto.response.ReportDetailResponse;
import the_monitor.application.dto.response.ReportListResponse;

import java.util.List;

public interface ReportService {

    List<ReportListResponse> getReportsByCreatedAt(Long clientId);

    List<ReportListResponse> getReportsByUpdatedAt(Long clientId);

    String deleteReports(Long clientId, Long reportId);

    ReportDetailResponse getReportDetail(Long clientId, Long reportId);

    String updateReport(Long clientId, Long reportId, ReportArticleUpdateRequest request);

}

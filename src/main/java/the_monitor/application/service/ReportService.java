package the_monitor.application.service;

import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.ReportArticleUpdateRequest;
import the_monitor.application.dto.response.ReportArticlesResponse;
import the_monitor.application.dto.response.ReportDetailResponse;
import the_monitor.application.dto.response.ReportListResponse;

import java.util.List;

public interface ReportService {

    List<ReportListResponse> getReportsByCreatedAt(Long clientId);

    List<ReportListResponse> getReportsByUpdatedAt(Long clientId);

    String deleteReports(Long clientId, Long reportId);

    ReportDetailResponse getReportDetail(Long clientId, Long reportId);

    ReportArticlesResponse getReportArticles(Long clientId, Long reportId);

    String updateReportArticle(Long clientId, Long reportId, ReportArticleUpdateRequest request);

    String updateReportTitle(Long clientId, Long reportId, String title);

    String updateReportColor(Long clientId, Long reportId, String color);

    String updateReportLogo(Long clientId, Long reportId, MultipartFile logo);

}

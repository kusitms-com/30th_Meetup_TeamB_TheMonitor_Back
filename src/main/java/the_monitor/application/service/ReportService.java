package the_monitor.application.service;

import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.*;
import the_monitor.application.dto.response.*;

import java.util.List;

public interface ReportService {

    List<ReportListResponse> getReports(Long clientId);

    String createReports(Long clientId, ReportCreateRequest request, MultipartFile logo);

    String deleteReports(Long clientId, Long reportId);

    ReportDetailResponse getReportDetail(Long clientId, Long reportId);

    String updateReportArticle(Long clientId, Long reportId, ReportArticleUpdateRequest request);

    String deleteReportArticle(Long clientId, Long reportId, Long reportArticleId);

    String updateReportArticleSummary(Long clientId, Long reportId, Long reportArticleId, ReportUpdateSummaryRequest request);

    String updateReportTitle(Long clientId, Long reportId, ReportUpdateTitleRequest request);

    String updateReportColor(Long clientId, Long reportId, ReportUpdateColorRequest request);

    String updateReportLogo(Long clientId, Long reportId, MultipartFile logo);

    List<ReportListResponse> searchReport(Long clientId, ReportSearchTitleRequest request);

    ReportCategoryTypeListResponse getReportCategoryList(Long clientId, Long reportId);

}

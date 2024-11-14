package the_monitor.presentation;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.ReportArticleUpdateRequest;
import the_monitor.application.dto.response.ReportDetailResponse;
import the_monitor.application.dto.response.ReportListResponse;
import the_monitor.application.service.ReportService;
import the_monitor.common.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/c")
    public ApiResponse<List<ReportListResponse>> getReportsByCreatedAt(@RequestParam("clientId") Long clientId) {

        return ApiResponse.onSuccessData("보고서 목록 최신순", reportService.getReportsByCreatedAt(clientId));

    }

    @GetMapping("/u")
    public ApiResponse<List<ReportListResponse>> getReportsByUpdatedAt(@RequestParam("clientId") Long clientId) {

        return ApiResponse.onSuccessData("보고서 목록 수정일순", reportService.getReportsByUpdatedAt(clientId));

    }

    @DeleteMapping()
    public ApiResponse<String> deleteReports(@RequestParam("clientId") Long clientId,
                                             @RequestParam("reportId") Long reportId) {
        return ApiResponse.onSuccess(reportService.deleteReports(clientId, reportId));

    }

    @GetMapping("/details")
    public ApiResponse<ReportDetailResponse> getReportDetail(@RequestParam("clientId") Long clientId,
                                                             @RequestParam("reportId") Long reportId) {
        return ApiResponse.onSuccessData("보고서 상세 조회", reportService.getReportDetail(clientId, reportId));

    }

    @PostMapping("/update")
    public ApiResponse<String> updateReportArticle(@RequestParam("clientId") Long clientId,
                                                   @RequestParam("reportId") Long reportId,
                                                   @RequestBody @Valid ReportArticleUpdateRequest request) {

        return ApiResponse.onSuccess(reportService.updateReport(clientId, reportId, request));

    }

}

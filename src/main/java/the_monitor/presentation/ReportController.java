package the_monitor.presentation;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.ReportArticleUpdateRequest;
import the_monitor.application.dto.request.ReportCreateRequest;
import the_monitor.application.dto.response.ReportArticlesResponse;
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

    @Operation(summary = "보고서 목록 최신순", description = "보고서 목록을 최신순으로 조회합니다.")
    @GetMapping()
    public ApiResponse<List<ReportListResponse>> getReports(@RequestParam("clientId") Long clientId) {

        return ApiResponse.onSuccessData("보고서 목록", reportService.getReports(clientId));

    }

    @Operation(summary = "보고서 생성", description = "보고서를 생성합니다.")
    @PostMapping()
    public ApiResponse<String> createReports(@RequestParam("clientId") Long clientId,
                                             @RequestBody ReportCreateRequest request) {

        return ApiResponse.onSuccess(reportService.createReports(clientId, request));

    }

    @Operation(summary = "보고서 삭제", description = "보고서를 삭제합니다.")
    @DeleteMapping()
    public ApiResponse<String> deleteReports(@RequestParam("clientId") Long clientId,
                                             @RequestParam("reportId") Long reportId) {
        return ApiResponse.onSuccess(reportService.deleteReports(clientId, reportId));

    }

    @Operation(summary = "보고서 상세 조회", description = "보고서 상세 정보를 조회합니다.")
    @GetMapping("/details")
    public ApiResponse<ReportDetailResponse> getReportDetail(@RequestParam("clientId") Long clientId,
                                                             @RequestParam("reportId") Long reportId) {
        return ApiResponse.onSuccessData("보고서 상세 조회", reportService.getReportDetail(clientId, reportId));

    }

    @Operation(summary = "보고서 기사 조회", description = "보고서에 포함된 기사를 조회합니다.")
    @GetMapping("/articles")
    public ApiResponse<ReportArticlesResponse> getReportArticles(@RequestParam("clientId") Long clientId,
                                                                 @RequestParam("reportId") Long reportId) {
        return ApiResponse.onSuccessData("보고서 기사 조회", reportService.getReportArticles(clientId, reportId));

    }

    @Operation(summary = "보고서 기사 수동 추가", description = "보고서에 포함된 기사를 수동 추가합니다.")
    @PostMapping("/articles/update")
    public ApiResponse<String> updateReportArticle(@RequestParam("clientId") Long clientId,
                                                   @RequestParam("reportId") Long reportId,
                                                   @RequestBody @Valid ReportArticleUpdateRequest request) {

        return ApiResponse.onSuccess(reportService.updateReportArticle(clientId, reportId, request));

    }

    @Operation(summary = "보고서 기사 삭제", description = "보고서에 포함된 기사를 삭제합니다.")
    @DeleteMapping("/articles/delete")
    public ApiResponse<String> deleteReportArticle(@RequestParam("clientId") Long clientId,
                                                   @RequestParam("reportId") Long reportId,
                                                   @RequestParam("reportArticleId") Long reportArticleId) {

        return ApiResponse.onSuccess(reportService.deleteReportArticle(clientId, reportId, reportArticleId));

    }

    @Operation(summary = "보고서 요약 수정", description = "보고서 요약을 수정합니다.")
    @PatchMapping("/articles/summary")
    public ApiResponse<String> updateReportArticleSummary(@RequestParam("clientId") Long clientId,
                                                          @RequestParam("reportId") Long reportId,
                                                          @RequestParam("reportArticleId") Long reportArticleId,
                                                          @RequestParam("summary") String summary) {

        return ApiResponse.onSuccess(reportService.updateReportArticleSummary(clientId, reportId, reportArticleId, summary));

    }

    @Operation(summary = "보고서 제목 수정", description = "보고서 제목을 수정합니다.")
    @PatchMapping("/title")
    public ApiResponse<String> updateReportTitle(@RequestParam("clientId") Long clientId,
                                                 @RequestParam("reportId") Long reportId,
                                                 @RequestParam("title") String title) {

        return ApiResponse.onSuccess(reportService.updateReportTitle(clientId, reportId, title));

    }

    @Operation(summary = "보고서 색상 수정", description = "보고서 색상을 수정합니다.")
    @PatchMapping("/color")
    public ApiResponse<String> updateReportColor(@RequestParam("clientId") Long clientId,
                                                 @RequestParam("reportId") Long reportId,
                                                 @RequestParam("color") String color) {

        return ApiResponse.onSuccess(reportService.updateReportColor(clientId, reportId, color));

    }

    @Operation(summary = "보고서 로고 수정", description = "보고서 로고를 수정합니다.")
    @PatchMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateReportLogo(@RequestParam("clientId") Long clientId,
                                                @RequestParam("reportId") Long reportId,
                                                @RequestPart("logo") MultipartFile logo) {

        return ApiResponse.onSuccess(reportService.updateReportLogo(clientId, reportId, logo));

    }

    @Operation(summary = "보고서 검색", description = "보고서를 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<List<ReportListResponse>> searchReport(@RequestParam("clientId") Long clientId,
                                                       @RequestParam("searchTitle") String searchTitle) {

        return ApiResponse.onSuccessData("보고서 검색", reportService.searchReport(clientId, searchTitle));

    }

}

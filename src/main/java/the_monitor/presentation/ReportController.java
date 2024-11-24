package the_monitor.presentation;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.*;
import the_monitor.application.dto.response.*;
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> createReports(@RequestPart("clientId") Long clientId,
                                             @RequestPart ReportCreateRequest request,
                                             @RequestPart(value = "logo", required = false) MultipartFile logo) {

        return ApiResponse.onSuccess(reportService.createReports(clientId, request, logo));

    }

    @Operation(summary = "보고서 삭제", description = "보고서를 삭제합니다.")
    @DeleteMapping()
    public ApiResponse<String> deleteReports(@RequestParam("clientId") Long clientId,
                                             @RequestParam("reportId") Long reportId) {
        return ApiResponse.onSuccess(reportService.deleteReports(clientId, reportId));

    }

    // Response 변경 요망
    @Operation(summary = "보고서 상세 조회", description = "보고서 상세 정보를 조회합니다.")
    @GetMapping("/details")
    public ApiResponse<ReportDetailResponse> getReportDetail(@RequestParam("clientId") Long clientId,
                                                             @RequestParam("reportId") Long reportId) {
        return ApiResponse.onSuccessData("보고서 상세 조회", reportService.getReportDetail(clientId, reportId));

    }

    // 수동 추가 시 Default로 넣어야 함.
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
                                                          @RequestBody ReportUpdateSummaryRequest request) {

        return ApiResponse.onSuccess(reportService.updateReportArticleSummary(clientId, reportId, reportArticleId, request));

    }

    @Operation(summary = "보고서 Head Content 수정", description = "보고서 제목, 색상, 로고를 수정합니다. (로고 빼고 바뀌는 거 없으면 그대로 주세요. null (x)")
    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateReportHeadContents(@RequestParam("clientId") Long clientId,
                                                        @RequestParam("reportId") Long reportId,
                                                        @RequestPart ReportUpdateHeadContentsRequest request,
                                                        @RequestPart(value = "logo", required = false) MultipartFile logo) {

        return ApiResponse.onSuccess(reportService.updateReportHeadContents(clientId, reportId, request, logo));

    }

    @Operation(summary = "보고서 검색", description = "보고서를 검색합니다.")
    @PostMapping("/search")
    public ApiResponse<List<ReportListResponse>> searchReport(@RequestParam("clientId") Long clientId,
                                                       @RequestBody ReportSearchTitleRequest request) {

        return ApiResponse.onSuccessData("보고서 검색", reportService.searchReport(clientId, request));

    }

    @Operation(summary = "보고서 카테고리 조회", description = "보고서 카테고리를 조회합니다.")
    @GetMapping("/categories")
    public ApiResponse<ReportCategoryTypeListResponse> getReportCategories(@RequestParam("clientId") Long clientId,
                                                                   @RequestParam("reportId") Long reportId) {

        return ApiResponse.onSuccessData("보고서 카테고리 조회", reportService.getReportCategoryList(clientId, reportId));

    }

}

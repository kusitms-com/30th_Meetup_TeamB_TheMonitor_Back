package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.ReportCategoryArticleDto;
import the_monitor.application.dto.request.ReportArticleUpdateRequest;
import the_monitor.application.dto.request.ReportCreateRequest;
import the_monitor.application.dto.response.ReportArticlesResponse;
import the_monitor.application.dto.response.ReportDetailResponse;
import the_monitor.application.dto.response.ReportListResponse;
import the_monitor.application.service.*;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.ReportArticleRepository;
import the_monitor.domain.repository.ReportRepository;
import the_monitor.domain.repository.ScrapRepository;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportArticleRepository reportArticleRepository;

    private final AccountService accountService;
    private final ClientService clientService;

    private final S3Service s3Service;

    @Override
    public List<ReportListResponse> getReports(Long clientId) {

        Client client = findClientById(clientId);

        return client.getReports().stream()
                .map(report -> ReportListResponse.builder()
                        .reportId(report.getId())
                        .title(report.getTitle())
                        .createdAt(String.valueOf(report.getCreatedAt()))
                        .updatedAt(String.valueOf(report.getUpdatedAt()))
                        .build())
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public String createReports(Long clientId, ReportCreateRequest request) {

        Client client = findClientById(clientId);

        String logoUrl = s3Service.uploadFile(request.getLogo());

        Report report = reportRepository.save(request.toEntity(client, logoUrl));

        // 각 카테고리별로 ReportArticle 생성 및 저장
        createAndSaveReportArticlesByCategories(report, request);


        reportRepository.save(report); // Report와 관련된 ReportArticles 자동 저장

        return "보고서 생성 성공";
    }

    @Override
    @Transactional
    public String deleteReports(Long clientId, Long reportId) {

        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), findByClientIdAndReportId(clientId, reportId));
        reportRepository.deleteById(reportId);
        return "보고서 삭제 성공";

    }

    @Override
    public ReportDetailResponse getReportDetail(Long clientId, Long reportId) {

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        return ReportDetailResponse.builder()
                .reportId(report.getId())
                .title(report.getTitle())
                .logo(report.getLogo())
                .color(report.getColor())
                .build();

    }

    @Override
    public ReportArticlesResponse getReportArticles(Long clientId, Long reportId) {

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        return ReportArticlesResponse.builder()
                .reportArticles(getCategorizedArticles(report))
                .build();

    }

    @Override
    @Transactional
    public String updateReportArticle(Long clientId, Long reportId, ReportArticleUpdateRequest request) {

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        reportArticleRepository.save(request.toEntity(report));

        return "보고서 기사 추가 완료";

    }

    @Override
    @Transactional
    public String deleteReportArticle(Long clientId, Long reportId, Long reportArticleId) {

            Report report = findByClientIdAndReportId(clientId, reportId);
            validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

            reportArticleRepository.deleteById(reportArticleId);

            return "보고서 기사 삭제 완료";

    }

    @Override
    @Transactional
    public String updateReportArticleSummary(Long clientId, Long reportId, Long reportArticleId, String summary) {

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        ReportArticle reportArticle = reportArticleRepository.findById(reportArticleId)
                .orElseThrow(() -> new ApiException(ErrorStatus._REPORT_ARTICLE_NOT_FOUND));

        validContentLength(summary);

        reportArticle.updateSummary(summary);

        return "��고서 기사 요약 수정 완료";

    }


    @Override
    @Transactional
    public String updateReportTitle(Long clientId, Long reportId, String title) {

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        report.updateTitle(title);

        return "보고서 제목 수정 완료";

    }

    @Override
    @Transactional
    public String updateReportColor(Long clientId, Long reportId, String color) {

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        report.updateColor(color);

        return "보고서 색상 수정 완료";

    }

    @Override
    @Transactional
    public String updateReportLogo(Long clientId, Long reportId, MultipartFile logo) {

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        report.updateLogo(s3Service.updateFile(report.getLogo(), logo));

        return "보고서 로고 수정 완료";

    }

    @Override
    public List<ReportListResponse> searchReport(Long clientId, String searchTitle) {

        List<Report> reports = reportRepository.findByClientIdAndTitleContaining(clientId, searchTitle);

        return reports.stream()
                .map(report -> ReportListResponse.builder()
                        .reportId(report.getId())
                        .title(report.getTitle())
                        .createdAt(String.valueOf(report.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());

    }

    // ReportCreateRequest로부터 ReportArticle 생성 및 저장
    private void createAndSaveReportArticlesByCategories(Report report, ReportCreateRequest request) {

        request.getReportArticles().forEach((categoryType, reportCategoryArticleDto) -> {
            reportCategoryArticleDto.getReportArticles().forEach((category, reportArticleDtoList) -> {
                reportArticleDtoList.forEach(reportArticleDto -> {
                    ReportArticle reportArticle = reportArticleDto.toEntity(report);

                    validContentLength(reportArticle.getSummary());

                    reportArticle.updateCategoryType(categoryType);
                    report.addReportArticle(reportArticle); // Report에 추가
                });
            });
        });

    }

    // ReportArticle 리스트를 CategoryType으로 그룹화
    private Map<CategoryType, List<ReportArticle>> getCategorizedArticles(Report report) {
        return report.getReportArticles().stream()
                .collect(Collectors.groupingBy(
                        ReportArticle::getCategoryType,
                        Collectors.mapping(reportArticle -> ReportArticle.builder()
                                .title(reportArticle.getTitle())
                                .url(reportArticle.getUrl())
                                .publisherName(reportArticle.getPublisherName())
                                .reporterName(reportArticle.getReporterName())
                                .publishDate(reportArticle.getPublishDate())
                                .report(report)
                                .build(), Collectors.toList())
                ));
    }

    private Long getAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAccountId();
    }

    private Account getAccountFromId(Long accountId) {
        return accountService.findAccountById(accountId);
    }

    private Client findClientById(Long clientId) {
        return clientService.findClientById(clientId);
    }

    // Client ID와 Report ID로 Report 조회
    private Report findByClientIdAndReportId(Long clientId, Long reportId) {
        return reportRepository.findReportByClientIdAndReportId(clientId, reportId);
    }

    // Account가 보고서에 권한이 있는지 확인
    private void validIsAccountAuthorizedForReport(Account account, Report report) {
        if (!report.getClient().getAccount().equals(account)) {
            throw new ApiException(ErrorStatus._REPORT_FORBIDDEN);
        }
    }

    private void validContentLength(String content) {
        if (content.length() > 100)
            throw new ApiException(ErrorStatus._INVALID_REPORT_ARTICLE_SUMMARY_LENGTH);
    }

}

package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.ReportArticleUpdateRequest;
import the_monitor.application.dto.response.ReportArticlesResponse;
import the_monitor.application.dto.response.ReportDetailResponse;
import the_monitor.application.dto.response.ReportListResponse;
import the_monitor.application.service.AccountService;
import the_monitor.application.service.ClientService;
import the_monitor.application.service.ReportService;
import the_monitor.application.service.S3Service;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.ReportArticleRepository;
import the_monitor.domain.repository.ReportRepository;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public List<ReportListResponse> getReportsByCreatedAt(Long clientId) {
        return getSortedReports(clientId, Comparator.comparing(Report::getCreatedAt).reversed()); // 생성일 기준 내림차순
    }

    @Override
    public List<ReportListResponse> getReportsByUpdatedAt(Long clientId) {
        return getSortedReports(clientId, Comparator.comparing(Report::getUpdatedAt).reversed()); // 수정일 기준 내림차순
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

    private List<ReportListResponse> getSortedReports(Long clientId, Comparator<Report> comparator) {

        Client client = findClientById(clientId);

        return client.getReports().stream()
                .sorted(comparator)
                .map(report -> ReportListResponse.builder()
                        .reportId(report.getId())
                        .title(report.getTitle())
                        .createdAt(String.valueOf(report.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());
    }


    private Map<CategoryType, List<ReportArticle>> getCategorizedArticles(Report report) {
        return report.getReportArticles().stream()
                .collect(Collectors.groupingBy(
                        ReportArticle::getCategory,
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

    private Report findByClientIdAndReportId(Long clientId, Long reportId) {
        return reportRepository.findReportByClientIdAndReportId(clientId, reportId);
    }

    private Client findClientById(Long clientId) {
        return clientService.findClientById(clientId);
    }

    private void validIsAccountAuthorizedForReport(Account account, Report report) {
        if (!report.getClient().getAccount().equals(account)) {
            throw new ApiException(ErrorStatus._REPORT_FORBIDDEN);
        }
    }

}

package the_monitor.application.serviceImpl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.ReportArticleDto;
import the_monitor.application.dto.ScrapArticleDto;
import the_monitor.application.dto.request.ScrapIdsRequest;
import the_monitor.application.dto.request.ScrapReportArticleRequest;
import the_monitor.application.dto.response.ScrapArticleListResponse;
import the_monitor.application.dto.response.ScrapReportArticeResponse;
import the_monitor.application.service.ClientService;
import the_monitor.application.service.ScrapService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.Scrap;
import the_monitor.domain.repository.ScrapRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Getter
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapServiceImpl implements ScrapService {

    private final ScrapRepository scrapRepository;
    private final ClientService clientService;

    @Override
    public Scrap findById(Long scrapId) {
        return scrapRepository.findById(scrapId)
                .orElseThrow(() -> new ApiException(ErrorStatus._SCRAP_NOT_FOUND));
    }

    @Override
    @Transactional
    public ScrapReportArticeResponse scrapArticle(Long clientId, ScrapReportArticleRequest request) {

        Client client = findClientById(clientId);

        // Map<CategoryType, ArticleGoogleDto>을 처리하여 Scrap 리스트로 변환하고 저장
        List<Scrap> savedScraps = convertAndSaveScrap(request.getReportArticles(), client);

        // 모든 Scrap ID를 단일 리스트화
        List<Long> scrapIds = savedScraps.stream()
                .map(Scrap::getId)
                .collect(Collectors.toList());

        return ScrapReportArticeResponse.builder()
                .scrapIds(scrapIds)
                .build();

    }

    @Override
    public ScrapArticleListResponse getScrapArticleList(Long clientId, ScrapIdsRequest request) {

        Client client = findClientById(clientId);

        // Scrap ID 리스트로 Scrap 리스트 조회
        List<Scrap> scrapList = scrapRepository.findAllById(request.getScrapIds());

        // Scrap 리스트를 CategoryType별로 ReportArticleDto로 변환 및 그룹화
        Map<CategoryType, List<ReportArticleDto>> reportArticleDtoMap = scrapList.stream()
                .collect(Collectors.groupingBy(Scrap::getCategoryType,
                        Collectors.mapping(this::convertToReportArticleDto, Collectors.toList())
                ));

        return ScrapArticleListResponse.builder()
                .reportArticles(reportArticleDtoMap)
                .build();
    }

    private Client findClientById(Long clientId) {
        return clientService.findClientById(clientId);
    }

    // Article -> Scrap 저장
    private List<Scrap> convertAndSaveScrap(List<ScrapArticleDto> reportArticles, Client client) {

        return reportArticles.stream()
                .map((articleDto) -> {
                    Scrap scrap = Scrap.builder()
                            .client(client)
                            .categoryType(articleDto.getCategoryType())
                            .title(articleDto.getTitle())
                            .url(articleDto.getUrl())
                            .publishDate(articleDto.getPublishDate())
                            .publisherName(articleDto.getPublisherName())
                            .reporterName(articleDto.getReporterName())
                            .build();
                    return scrapRepository.save(scrap);
                })
                .collect(Collectors.toList());

    }

    // Scrap -> ReportArticleDto 변환
    private ReportArticleDto convertToReportArticleDto(Scrap scrap) {
        return ReportArticleDto.builder()
                .publishedDate(scrap.getPublishDate())
                .keyword(scrap.getCategoryType().name())  // CategoryType을 문자열로 변환
                .headLine(scrap.getTitle())
                .url(scrap.getUrl())
                .media(scrap.getPublisherName())
                .reporter(scrap.getReporterName())
                .build();
    }

}

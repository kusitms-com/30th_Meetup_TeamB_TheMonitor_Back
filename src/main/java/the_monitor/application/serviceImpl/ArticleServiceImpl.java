package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.ArticleGoogleDto;
import the_monitor.application.dto.request.ArticleRequest;
import the_monitor.application.dto.request.ArticleSearchRequest;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.service.ArticleService;
import the_monitor.application.service.GoogleSearchService;
import the_monitor.application.service.KeywordService;
import the_monitor.common.PageResponse;
import the_monitor.domain.model.Article;
import the_monitor.domain.model.Keyword;
import the_monitor.domain.repository.ArticleRepository;
import the_monitor.infrastructure.jwt.JwtProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final GoogleSearchService googleSearchService;
    private final ArticleRepository articleRepository;
    private final JwtProvider jwtProvider;
    private final KeywordService keywordService;

    private static final int ARTICLE_SAVE_START_TIME = 9;
    private static final int ARTICLE_SAVE_END_TIME = 7;

    @Scheduled(cron = "0 0 7 * * *")
    @Transactional
    public void dailyMonitoring() {

        List<Keyword> keywords = keywordService.getAllKeywords();

        for (Keyword keyword : keywords) {
            saveArticlesFromGoogle(keyword);
        }

    }

    private void saveArticlesFromGoogle(Keyword keyword) {

        List<ArticleGoogleDto> articleDtos = googleSearchService.toDto(keyword);

        LocalDateTime startTime = LocalDateTime.now().minusDays(1).withHour(ARTICLE_SAVE_START_TIME).withMinute(0);
        LocalDateTime endTime = LocalDateTime.now().withHour(ARTICLE_SAVE_END_TIME).withMinute(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        for (ArticleGoogleDto dto : articleDtos) {
            if (!dto.getPublishDate().isEmpty()) {
                LocalDateTime publishDate = LocalDateTime.parse(dto.getPublishDate(), formatter);

                if (publishDate.isAfter(startTime) && publishDate.isBefore(endTime)) {
                    articleRepository.save(dto.toEntity(keyword));
                }
            }
        }

    }

    @Override
    public PageResponse<ArticleResponse> getDefaultArticles(String token, ArticleRequest request) {

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Long accountId = jwtProvider.getAccountId(token);
        Page<Article> articles = articleRepository.findAllByAccountIdAndAndCategoryId(accountId, 1L, pageable);

        return getArticlePageResponse(articles);

    }

    @Override
    public PageResponse<ArticleResponse> getArticlesBySearch(String token, ArticleSearchRequest request) {

        List<ArticleResponse> articles = googleSearchService.searchArticlesWithoutSaving(
                request.getKeyword(), request.getDateRestrict(), request.getPage(), request.getSize());

        return PageResponse.<ArticleResponse>builder()
                .listPageResponse(articles)
                .totalCount((long) articles.size())
                .size(articles.size())
                .build();

    }

    private PageResponse<ArticleResponse> getArticlePageResponse(Page<Article> articles) {

        List<ArticleResponse> articleResponses = articles.stream()
                .map(article -> ArticleResponse.builder()
                        .title(article.getTitle())
                        .body(article.getBody())
                        .url(article.getUrl())
                        .image(article.getImage())
                        .publisherName(article.getPublisherName())
                        .reporterName(article.getReporterName())
                        .publishDate(article.getPublishDate())
                        .build())
                .toList();

        return PageResponse.<ArticleResponse>builder()
                .listPageResponse(articleResponses)
                .totalCount(articles.getTotalElements())
                .size(articleResponses.size())
                .build();

    }

}
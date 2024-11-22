package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.service.ArticleService;
import the_monitor.application.service.GoogleSearchService;
import the_monitor.application.service.KeywordService;
import the_monitor.common.PageResponse;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Account;
import the_monitor.domain.model.Keyword;
import the_monitor.domain.repository.ReportArticleRepository;
import the_monitor.infrastructure.jwt.JwtProvider;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final GoogleSearchService googleSearchService;
    private final ReportArticleRepository articleRepository;
    private final JwtProvider jwtProvider;
    private final KeywordService keywordService;

    private static final int ARTICLE_SAVE_START_TIME = 9;
    private static final int ARTICLE_SAVE_END_TIME = 7;

//    @Scheduled(cron = "0 0 7 * * *")
//    @Transactional
//    public void dailyMonitoring() {
//
//        List<Keyword> keywords = keywordService.getAllKeywords();
//
//        for (Keyword keyword : keywords) {
//
//            saveArticlesFromGoogle(keyword);
//
//        }
//
//    }

//    private void saveArticlesFromGoogle(Keyword keyword) {
//
//        List<ArticleGoogleDto> articleDtos = googleSearchService.toDto(keyword);
//
//        LocalDateTime startTime = LocalDateTime.now().minusDays(1).withHour(ARTICLE_SAVE_START_TIME).withMinute(0);
//        LocalDateTime endTime = LocalDateTime.now().withHour(ARTICLE_SAVE_END_TIME).withMinute(0);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//
//        for (ArticleGoogleDto dto : articleDtos) {
//            if (!dto.getPublishDate().isEmpty()) {
//                LocalDateTime publishDate = LocalDateTime.parse(dto.getPublishDate(), formatter);
//
//                if (publishDate.isAfter(startTime) && publishDate.isBefore(endTime)) {
//                    articleRepository.save(dto.toEntity(keyword));
//                }
//            }
//        }
//
//    }

    @Override
    public PageResponse<ArticleResponse> getArticlesGroupByCategory(Long clientId, CategoryType categoryType, int page) {

        Long accountId = getAccountId();

        // Account ID, Client ID, Category Type에 따른 키워드 목록 가져오기
        List<Keyword> keywords = keywordService.getKeywordByAccountIdAndClientIdAndCategoryType(accountId, clientId, categoryType);

        // 키워드들을 OR 연산자로 묶기
        String combinedKeywords = keywords.stream()
                .map(Keyword::getKeyword)
                .reduce((keyword1, keyword2) -> keyword1 + " OR " + keyword2)
                .orElse(""); // 키워드가 없을 경우 빈 문자열 반환

        if (combinedKeywords.isEmpty()) {
            return PageResponse.<ArticleResponse>builder()
                    .listPageResponse(new ArrayList<>())
                    .totalCount(0L)
                    .size(0)
                    .build();
        }

        // OR로 묶은 키워드를 기반으로 검색
        ArticleResponse combinedArticleResponse = googleSearchService.searchArticlesWithoutSaving(combinedKeywords, "w1", page, 10);

        return PageResponse.<ArticleResponse>builder()
                .listPageResponse(List.of(combinedArticleResponse))
                .totalCount((long) combinedArticleResponse.getTotalResults())
                .size(10)
                .build();

    }


    @Override
    public PageResponse<ArticleResponse> getArticlesBySearch(String keyword, int page) {

        ArticleResponse articleResponse = googleSearchService.searchArticlesWithoutSaving(keyword, "w1", page, 10);

        return PageResponse.<ArticleResponse>builder()
                .listPageResponse(List.of(articleResponse))
                .totalCount((long) articleResponse.getTotalResults())
                .size(10)
                .build();

    }


    private Long getAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAccountId();
    }

}
package the_monitor.application.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.ArticleGoogleDto;
import the_monitor.application.dto.ArticleNaverDto;
import the_monitor.application.service.AccountService;
import the_monitor.application.service.GoogleSearchService;
import the_monitor.application.service.NaverSearchService;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.ArticleRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleServiceImpl {

    private final int ARTICLE_SAVE_START_TIME = 9;
    private final int ARTICLE_SAVE_END_TIME = 7;

    private final ObjectMapper objectMapper;
    private final NaverSearchService naverSearchService;
    private final GoogleSearchService googleSearchService;
    private final AccountService accountService;
    private final ArticleRepository articleRepository;

    @Transactional
    @Scheduled(cron = "0 0 7 * * ?")
    public void dailyMonitoring() {
        List<Account> accounts = accountService.getAccountList();

        for (Account account : accounts) {
            List<Client> clients = account.getClients();
            for (Client client : clients) {
                List<Category> categories = client.getCategories();
                for (Category category : categories) {
                    List<Keyword> keywords = category.getKeywords();
                    for (Keyword keyword : keywords) {
                        // 네이버 검색 결과 저장
                        saveArticlesFromNaver(keyword);

                        // 구글, 다음 등 다른 포털의 검색 결과도 동일하게 저장
                        saveArticlesFromGoogle(keyword);
//                        saveArticlesFromDaum(keyword);
                    }
                }
            }
        }
    }

    private void saveArticlesFromNaver(Keyword keyword) {

        List<ArticleNaverDto> articleDtos = naverSearchService.toDto(keyword);

        LocalDateTime startTime = LocalDateTime.now().minusDays(1).withHour(ARTICLE_SAVE_START_TIME).withMinute(0);
        LocalDateTime endTime = LocalDateTime.now().withHour(ARTICLE_SAVE_END_TIME).withMinute(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        for (ArticleNaverDto dto : articleDtos) {
            LocalDateTime publishDate = LocalDateTime.parse(dto.getPublishDate(), formatter);

            if (publishDate.isAfter(startTime) && publishDate.isBefore(endTime)) {
                Article article = dto.toEntity();
                articleRepository.save(article);
            }
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
                    Article article = dto.toEntity();
                    articleRepository.save(article);
                }
            }
        }
    }

//    private void saveArticlesFromDaum(Keyword keyword) {
//        List<ArticleDaumDto> articleDtos = daumSearchService.toDto(keyword);
//
//        for (ArticleDaumDto dto : articleDtos) {
//            Article article = dto.toEntity();
//            articleRepository.save(article);
//        }
//    }



}

package the_monitor.application.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import the_monitor.application.dto.ArticleGoogleDto;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.service.GoogleSearchService;
import the_monitor.domain.model.Keyword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleSearchServiceImpl implements GoogleSearchService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${GOOGLE_API_KEY}")
    private String apiKey;

    @Value("${GOOGLE_SEARCH_ENGINE_ID}")
    private String searchEngineId;

    @Value("${GOOGLE_API_BASE_URL:https://www.googleapis.com/customsearch/v1}")
    private String baseUrl;

    @Override
    public List<ArticleGoogleDto> toDto(Keyword keyword) {
        List<ArticleGoogleDto> allResults = new ArrayList<>();
        int start = 1;
        boolean hasMoreResults = true;

        while (hasMoreResults) {
            List<ArticleGoogleDto> pageResults = searchArticles(keyword.getKeyword(), "d1", start);

            if (pageResults.isEmpty()) {
                hasMoreResults = false;
            } else {
                allResults.addAll(pageResults);
                start += 10;
            }
        }

        return allResults;
    }

    @Override
    public List<ArticleResponse> searchArticlesWithoutSaving(String keyword, String dateRestrict, int page, int size) {
        // `page`와 `size`에 따라 `start` 값을 계산합니다.
        int start = (page - 1) * size + 1; // 페이지가 1일 때 1부터 시작

        // 지정된 페이지의 결과만 가져오기
        List<ArticleGoogleDto> articleDtos = searchArticles(keyword, dateRestrict, start);
        return mapToArticleResponseList(articleDtos);
    }

    public List<ArticleGoogleDto> searchArticles(String query, String dateRestrict, int start) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("q", query)
                .queryParam("key", apiKey)
                .queryParam("cx", searchEngineId)
                .queryParam("num", 10)
                .queryParam("start", start)
                .queryParam("dateRestrict", dateRestrict)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return parseResponse(response.getBody());
        } else {
            throw new RuntimeException("Failed to search Google: " + response.getStatusCode());
        }
    }

    private List<ArticleGoogleDto> parseResponse(String jsonResponse) {
        List<ArticleGoogleDto> searchDetails = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode items = root.path("items");

            for (JsonNode item : items) {
                String title = item.path("title").asText();
                String snippet = item.path("snippet").asText();
                String link = item.path("link").asText();
                String imageUrl = item.path("pagemap").path("cse_image").get(0).path("src").asText();
                String publisher = item.path("pagemap").path("metatags").get(0).path("og:site_name").asText();
                String publishDate = item.path("pagemap").path("metatags").get(0).path("article:published_time").asText();
                String reporter = item.path("pagemap").path("metatags").get(0).path("dable:author").asText();

                ArticleGoogleDto dto = ArticleGoogleDto.builder()
                        .title(title)
                        .body(snippet)
                        .url(link)
                        .imageUrl(imageUrl)
                        .publisherName(publisher)
                        .publishDate(publishDate)
                        .reporterName(reporter)
                        .build();

                searchDetails.add(dto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchDetails;
    }

    private List<ArticleResponse> mapToArticleResponseList(List<ArticleGoogleDto> articleDtos) {
        List<ArticleResponse> articleResponses = new ArrayList<>();
        for (ArticleGoogleDto dto : articleDtos) {
            articleResponses.add(ArticleResponse.builder()
                    .title(dto.getTitle())
                    .body(dto.getBody())
                    .url(dto.getUrl())
                    .image(dto.getImageUrl())
                    .publisherName(dto.getPublisherName())
                    .reporterName(dto.getReporterName())
                    .publishDate(dto.getPublishDate())
                    .build());
        }
        return articleResponses;
    }
}
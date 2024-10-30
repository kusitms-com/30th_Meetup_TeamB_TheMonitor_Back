package the_monitor.application.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import the_monitor.application.dto.ArticleGoogleDto;
import the_monitor.application.service.GoogleSearchService;
import the_monitor.domain.model.Keyword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleSearchServiceImpl implements GoogleSearchService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String searchEngineId;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public GoogleSearchServiceImpl(
            RestTemplate restTemplate,
            @Value("${GOOGLE_API_KEY}") String apiKey,
            @Value("${GOOGLE_SEARCH_ENGINE_ID}") String searchEngineId,
            @Value("${GOOGLE_API_BASE_URL:https://www.googleapis.com/customsearch/v1}") String baseUrl,
            ObjectMapper objectMapper) {

        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.searchEngineId = searchEngineId;
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ArticleGoogleDto> toDto(Keyword keyword) {
        String response = search(keyword.getKeyword());
        return parseResponse(response);
    }

    private String search(String query) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("q", query)
                .queryParam("key", apiKey)
                .queryParam("cx", searchEngineId)
                .queryParam("num", 10)  // 한 번에 표시할 결과 개수 (최대 10)
                .queryParam("start", 1)  // 시작 위치 (페이지네이션에 사용)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
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
                // 기본 정보 추출
                String title = item.path("title").asText();
                String snippet = item.path("snippet").asText();
                String link = item.path("link").asText();

                // `pagemap`의 `metatags`에서 추가 정보 추출
                String imageUrl = "";
                String publisher = "";
                String publishDate = "";
                String reporter = "";

                JsonNode metatags = item.path("pagemap").path("metatags");
                if (metatags.isArray() && metatags.size() > 0) {
                    JsonNode meta = metatags.get(0);

                    imageUrl = meta.path("og:image").asText(); // 대표 이미지
                    publisher = meta.path("og:site_name").asText(); // 출판사
                    publishDate = meta.path("article:published_time").asText(); // 출간일
                    reporter = meta.path("dable:author").asText(); // 기자
                }

                // ArticleGoogleDto 객체 생성
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

}
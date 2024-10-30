package the_monitor.application.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import the_monitor.application.dto.ArticleNaverDto;
import the_monitor.application.service.NaverSearchService;
import the_monitor.domain.model.Keyword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NaverSearchServiceImpl implements NaverSearchService {

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;
    private final String baseUrl;

    private final ObjectMapper objectMapper;

    public NaverSearchServiceImpl(
            RestTemplate restTemplate,
            @Value("${NAVER_API_CLIENT_ID}") String clientId,
            @Value("${NAVER_API_CLIENT_SECRET}") String clientSecret,
            @Value("${NAVER_API_BASE_URL:https://openapi.naver.com/v1/search/}") String baseUrl,
            ObjectMapper objectMapper) {

        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;

    }

    @Override
    public List<ArticleNaverDto> toDto(Keyword keyword) {

        String response = search(keyword.getKeyword());
        return parseResponse(response);

    }

    private String search(String query) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("query", query)
                .queryParam("display", 10)
                .queryParam("start", 1)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to search blog: " + response.getStatusCode());
        }
    }

    private List<ArticleNaverDto> parseResponse(String jsonResponse) {
        List<ArticleNaverDto> searchDetails = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode items = root.path("items");

            for (JsonNode item : items) {
                String title = item.path("title").asText();
                String url = item.path("originallink").asText();
                String body = item.path("description").asText();
                String publishDate = item.path("pubDate").asText();

                // ArticleNaverDto 객체로 변환하여 리스트에 추가
                ArticleNaverDto dto = ArticleNaverDto.builder()
                        .title(title)
                        .body(body)
                        .url(url)
                        .publishDate(publishDate)
                        .build();
                searchDetails.add(dto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchDetails;
    }
}
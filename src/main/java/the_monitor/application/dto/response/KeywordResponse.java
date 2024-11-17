package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class KeywordResponse {

    private Map<CategoryType, List<String>> keywordsByCategory; // categoryId별 키워드 리스트

    @Builder
    public KeywordResponse(Map<CategoryType, List<String>> keywordsByCategory) {
        this.keywordsByCategory = keywordsByCategory;
    }

}

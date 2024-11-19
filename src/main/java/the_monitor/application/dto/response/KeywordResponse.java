package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class KeywordResponse {

    @JsonInclude
    private Map<CategoryType, List<String>> keywordsByCategory;

    @Builder
    public KeywordResponse(Map<CategoryType, List<String>> keywordsByCategory) {
        this.keywordsByCategory = keywordsByCategory;
    }

}

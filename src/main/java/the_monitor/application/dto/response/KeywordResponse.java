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

    private CategoryType categoryType;
    private List<KeywordAndIdResponse> keywordAndIdResponses;

    @Builder
    public KeywordResponse(CategoryType categoryType,
                           List<KeywordAndIdResponse> keywordAndIdResponses) {

        this.categoryType = categoryType;
        this.keywordAndIdResponses = keywordAndIdResponses;

    }

}

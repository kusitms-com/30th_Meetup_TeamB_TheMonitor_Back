package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KeywordAndIdResponse {

    private Long keywordId;
    private String keywordName;

    @Builder
    public KeywordAndIdResponse(Long keywordId,
                                String keywordName) {

        this.keywordId = keywordId;
        this.keywordName = keywordName;

    }

}

package the_monitor.application.service;

import the_monitor.application.dto.request.KeywordUpdateRequest;
import the_monitor.application.dto.response.KeywordResponse;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Keyword;

import java.util.List;
import java.util.Map;

public interface KeywordService {

    KeywordResponse getKeywords(Long clientId);

    List<Keyword> getKeywordByAccountIdAndClientIdAndCategoryType(Long accountId, Long clientId, CategoryType categoryType);

    KeywordResponse updateKeywords(Long clientId, KeywordUpdateRequest keywordUpdateRequest);

}

package the_monitor.application.service;

import the_monitor.application.dto.response.KeywordResponse;
import the_monitor.domain.model.Keyword;

import java.util.List;

public interface KeywordService {

    KeywordResponse getKeywords(Long clientId);

    List<Keyword> getKeywordByAccountIdAndClientIdAndCategoryId(Long accountId, Long clientId, Long categoryId);

}

package the_monitor.application.service;

import the_monitor.domain.model.Keyword;

import java.util.List;

public interface KeywordService {

    int getTotalSearchCount(int keywordId);

    List<Keyword> getAllKeywords();

}

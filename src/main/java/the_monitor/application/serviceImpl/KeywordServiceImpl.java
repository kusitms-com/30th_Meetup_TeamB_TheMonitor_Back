package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.service.KeywordService;
import the_monitor.domain.model.Keyword;
import the_monitor.domain.repository.KeywordRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordRepository keywordRepository;

    @Override
    public int getTotalSearchCount(int keywordId) {

        Keyword keyword = keywordRepository.findKeywordById((long) keywordId);

        return keyword.getNaverCount() + keyword.getGoogleCount() + keyword.getDaumCount();

    }

    @Override
    public List<Keyword> getAllKeywords() {

        return keywordRepository.findAll();

    }


}

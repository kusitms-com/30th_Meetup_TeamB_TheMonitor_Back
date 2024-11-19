package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.response.KeywordResponse;
import the_monitor.application.service.KeywordService;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Keyword;
import the_monitor.domain.repository.KeywordRepository;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordRepository keywordRepository;


    @Override
    public KeywordResponse getKeywords(Long clientId) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); // CustomUserDetails 캐스팅

//        Long accountId = userDetails.getAccountId();
        Long accountId = 2L;

        Map<CategoryType, List<String>> keywordsByCategory = new HashMap<>();

        for (CategoryType categoryType : CategoryType.values()) {
            System.out.printf("categoryType: %s", categoryType);
            List<Keyword> keywords = keywordRepository.findKeywordByAccountIdAndClientIdAndCategoryType(accountId, clientId, categoryType);
            System.out.printf("keywords: %s", keywords);

            List<String> keywordList = keywords.stream()
                    .map(Keyword::getKeyword)
                    .collect(Collectors.toList());
            keywordsByCategory.put(categoryType, keywordList);
        }

        return KeywordResponse.builder()
                .keywordsByCategory(keywordsByCategory)
                .build();

    }

    @Override
    public List<Keyword> getKeywordByAccountIdAndClientIdAndCategoryType(Long accountId, Long clientId, CategoryType categoryType) {

        return keywordRepository.findKeywordByAccountIdAndClientIdAndCategoryType(accountId, clientId, categoryType);

    }


}

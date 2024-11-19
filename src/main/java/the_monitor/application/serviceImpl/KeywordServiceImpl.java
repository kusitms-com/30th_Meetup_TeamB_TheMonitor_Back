package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.request.KeywordUpdateRequest;
import the_monitor.application.dto.response.KeywordResponse;
import the_monitor.application.service.KeywordService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.Keyword;
import the_monitor.domain.repository.ClientRepository;
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
    private final ClientRepository clientRepository;
    private final CategoryServiceImpl categoryServiceImpl;


    @Override
    public KeywordResponse getKeywords(Long clientId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); // CustomUserDetails 캐스팅

        Long accountId = userDetails.getAccountId();

        Map<CategoryType, List<String>> keywordsByCategory = new HashMap<>();

        for (CategoryType categoryType : CategoryType.values()) {
            List<Keyword> keywords = keywordRepository.findKeywordByAccountIdAndClientIdAndCategoryType(accountId, clientId, categoryType);

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

    @Transactional
    @Override
    public KeywordResponse updateKeywords(Long clientId, KeywordUpdateRequest keywordUpdateRequest) {
        Map<CategoryType, List<String>> keywordsByCategory = keywordUpdateRequest.getKeywordsByCategory();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long accountId = userDetails.getAccountId();

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));

        keywordRepository.deleteAllByClientId(clientId);

        for (Map.Entry<CategoryType, List<String>> entry : keywordsByCategory.entrySet()) {
            categoryServiceImpl.saveCategoryWithKeywords(entry.getKey(), entry.getValue(), client);
        }

        return KeywordResponse.builder()
                .keywordsByCategory(keywordsByCategory)
                .build();
    }
}

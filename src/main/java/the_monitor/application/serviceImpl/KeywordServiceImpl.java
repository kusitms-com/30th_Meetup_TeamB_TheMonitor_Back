package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.request.KeywordUpdateRequest;
import the_monitor.application.dto.response.KeywordAndIdResponse;
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

        Long accountId = getAccountIdFromAuthentication();

        return getKeywordResponses(accountId, clientId);

    }

    @Override
    public List<Keyword> getKeywordByAccountIdAndClientIdAndCategoryType(Long accountId, Long clientId, CategoryType categoryType) {

        return keywordRepository.findKeywordByAccountIdAndClientIdAndCategoryType(accountId, clientId, categoryType);

    }

    @Transactional
    @Override
    public KeywordResponse updateKeywords(Long clientId, KeywordUpdateRequest keywordUpdateRequest) {

        Map<CategoryType, List<String>> keywordsByCategory = keywordUpdateRequest.getKeywordsByCategory();

        Long accountId = getAccountIdFromAuthentication();

        // 클라이언트 확인
        Client client = findClientById(clientId);

        // 기존 키워드 삭제
        keywordRepository.deleteAllByClientId(clientId);

        // 새로운 키워드 저장
        for (Map.Entry<CategoryType, List<String>> entry : keywordsByCategory.entrySet()) {
            categoryServiceImpl.saveCategoryWithKeywords(entry.getKey(), entry.getValue(), client);
        }

        return getKeywordResponses(accountId, clientId);

    }

    private Long getAccountIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAccountId();
    }

    private Client findClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));
    }


    private KeywordResponse getKeywordResponses(Long accountId, Long clientId) {
        // 갱신된 키워드 가져오기
        List<Keyword> keywords = findKeywordByAccountIdAndClientId(accountId, clientId);

        // CategoryType별로 키워드를 분류
        List<KeywordAndIdResponse> selfKeywords = getSELFKeywordAndIdResponses(keywords);

        List<KeywordAndIdResponse> competitorKeywords = getCOMPETITORKeywordAndIdResponses(keywords);

        List<KeywordAndIdResponse> industryKeywords = getINDUSTRYKeywordAndIdResponses(keywords);

        // KeywordResponse 생성 및 반환
        return KeywordResponse.builder()
                .self(selfKeywords)
                .competitor(competitorKeywords)
                .industry(industryKeywords)
                .build();

    }

    private List<Keyword> findKeywordByAccountIdAndClientId(Long accountId, Long clientId) {
        return keywordRepository.findKeywordByAccountIdAndClientId(accountId, clientId);
    }

    private List<KeywordAndIdResponse> getSELFKeywordAndIdResponses(List<Keyword> keywords) {
        return keywords.stream()
                .filter(keyword -> keyword.getCategory().getCategoryType() == CategoryType.SELF)
                .map(keyword -> KeywordAndIdResponse.builder()
                        .keywordId(keyword.getId())
                        .keywordName(keyword.getKeyword())
                        .build())
                .toList();
    }

    private List<KeywordAndIdResponse> getCOMPETITORKeywordAndIdResponses(List<Keyword> keywords) {
        return keywords.stream()
                .filter(keyword -> keyword.getCategory().getCategoryType() == CategoryType.COMPETITOR)
                .map(keyword -> KeywordAndIdResponse.builder()
                        .keywordId(keyword.getId())
                        .keywordName(keyword.getKeyword())
                        .build())
                .toList();
    }

    private List<KeywordAndIdResponse> getINDUSTRYKeywordAndIdResponses(List<Keyword> keywords) {
        return keywords.stream()
                .filter(keyword -> keyword.getCategory().getCategoryType() == CategoryType.INDUSTRY)
                .map(keyword -> KeywordAndIdResponse.builder()
                        .keywordId(keyword.getId())
                        .keywordName(keyword.getKeyword())
                        .build())
                .toList();
    }

}

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
import the_monitor.application.service.AccountService;
import the_monitor.application.service.KeywordService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Account;
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
    private final AccountService accountService;


    @Override
    public KeywordResponse getKeywords() {

        Long accountId = getAccountIdFromAuthentication();

        Long clientId = getClientIdFromAuthentication();

        return getKeywordResponses(accountId, clientId);

    }

    @Override
    public List<Keyword> getKeywordByAccountIdAndClientIdAndCategoryType(Long accountId, CategoryType categoryType) {

        Long clientId = getClientIdFromAuthentication();

        return keywordRepository.findKeywordByAccountIdAndClientIdAndCategoryType(accountId, clientId, categoryType);

    }

    @Override
    public Keyword getKeywordByIdAndAccountIdAndClientIdAndCategoryType(Long keywordId, Long accountId, CategoryType categoryType) {

        Long clientId = getClientIdFromAuthentication();

        return keywordRepository.findByIdAndAccountIdAndClientIdAndCategoryType(keywordId, accountId, clientId, categoryType)
                .orElseThrow(() -> new IllegalArgumentException("Keyword not found with the given parameters"));

    }

    @Transactional
    @Override
    public KeywordResponse updateKeywords(KeywordUpdateRequest keywordUpdateRequest) {

        Long clientId = getClientIdFromAuthentication();

        Map<CategoryType, List<String>> keywordsByCategory = keywordUpdateRequest.getKeywordsByCategory();

        Long accountId = getAccountIdFromAuthentication(); // JWT에서 accountId 추출

        // accountId와 clientId를 함께 검증
        Client client = clientRepository.findByIdAndAccountId(clientId, accountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_FORBIDDEN));

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

    private Account findAccountById() {
        return accountService.findAccountById(getAccountIdFromAuthentication());
    }

    private Long getClientIdFromAuthentication() {
        Account account = findAccountById();
        return account.getSelectedClientId();
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
                        .categoryType(CategoryType.SELF)
                        .build())
                .toList();
    }

    private List<KeywordAndIdResponse> getCOMPETITORKeywordAndIdResponses(List<Keyword> keywords) {
        return keywords.stream()
                .filter(keyword -> keyword.getCategory().getCategoryType() == CategoryType.COMPETITOR)
                .map(keyword -> KeywordAndIdResponse.builder()
                        .keywordId(keyword.getId())
                        .keywordName(keyword.getKeyword())
                        .categoryType(CategoryType.COMPETITOR)
                        .build())
                .toList();
    }

    private List<KeywordAndIdResponse> getINDUSTRYKeywordAndIdResponses(List<Keyword> keywords) {
        return keywords.stream()
                .filter(keyword -> keyword.getCategory().getCategoryType() == CategoryType.INDUSTRY)
                .map(keyword -> KeywordAndIdResponse.builder()
                        .keywordId(keyword.getId())
                        .keywordName(keyword.getKeyword())
                        .categoryType(CategoryType.INDUSTRY)
                        .build())
                .toList();
    }



}

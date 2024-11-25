package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import the_monitor.application.dto.request.ClientRequest;
import the_monitor.application.dto.request.ClientUpdateRequest;
import the_monitor.application.dto.response.ClientGetResponse;
import the_monitor.application.dto.response.ClientResponse;
import the_monitor.application.dto.response.EmailSendResponse;
import the_monitor.application.dto.response.ReportListResponse;
import the_monitor.application.service.CategoryService;
import the_monitor.application.service.ClientService;
import org.springframework.stereotype.Service;
import the_monitor.application.service.S3Service;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.enums.KeywordType;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.*;
import the_monitor.infrastructure.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final CategoryService categoryService;
    private final AccountRepository accountRepository;
    private final KeywordRepository keywordRepository;

    private final ClientMailRecipientRepository clientMailRecipientRepository;
    private final ClientMailCCRepository clientMailCCRepository;

    private final JwtProvider jwtProvider;
    private final S3Service s3Service;
    private final EmailServiceImpl emailServiceImpl;

    @Value("${cloud.aws.s3.default-logo-url}")
    private String defaultLogoUrl;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, CategoryService categoryService, AccountRepository accountRepository, KeywordRepository keywordRepository,
                             ClientMailRecipientRepository clientMailRecipientRepository, ClientMailCCRepository clientMailCCRepository, JwtProvider jwtProvider, S3Service s3Service, EmailServiceImpl emailServiceImpl) {
        this.clientRepository = clientRepository;
        this.categoryService = categoryService;
        this.accountRepository = accountRepository;
        this.keywordRepository = keywordRepository;
        this.clientMailRecipientRepository = clientMailRecipientRepository;
        this.clientMailCCRepository = clientMailCCRepository;
        this.jwtProvider = jwtProvider;
        this.s3Service = s3Service;
        this.emailServiceImpl = emailServiceImpl;
    }

    @Transactional
    public ClientResponse createClient(ClientRequest clientRequest, MultipartFile logo) {

        // JWT에서 accountId를 추출하는 과정
        Long extractedAccountId = getAccountIdFromJwt();
        Account account = accountRepository.findById(extractedAccountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND));

        String logoPath;
        logoPath = (logo != null) ? s3Service.uploadFile(logo) : defaultLogoUrl;

        // 클라이언트 객체 생성
        Client client = Client.builder()
                .name(clientRequest.getName())
                .managerName(clientRequest.getManagerName())
                .logo(logoPath)
                .account(account)
                .build();

        client = clientRepository.save(client);

        // 카테고리 및 키워드 저장
        Map<CategoryType, List<String>> categoryKeywords = clientRequest.getCategoryKeywords();
        for (Map.Entry<CategoryType, List<String>> entry : categoryKeywords.entrySet()) {
            categoryService.saveCategoryWithKeywords(entry.getKey(), entry.getValue(), client); // CategoryService 사용
        }

        // 이메일 수신자와 참조인 저장
        emailServiceImpl.saveEmails(clientRequest.getRecipientEmails(), clientRequest.getCcEmails(), client);

        // ClientResponse 반환
        return ClientResponse.builder()
                .clientId(client.getId())
                .name(client.getName())
                .managerName(client.getManagerName())
                .logoUrl(client.getLogo())
                .build();
    }
    @Override
    public List<ClientResponse> getClientsByAccountId() {
        Long accountId = getAccountIdFromJwt(); // JWT에서 accountId 추출
        List<Client> clients = clientRepository.findAllByAccountId(accountId);
        if (clients.isEmpty()) {
            return List.of();
        }

        return clients.stream()
                .map(client -> ClientResponse.builder()
                        .clientId(client.getId())
                        .name(client.getName())
                        .managerName(client.getManagerName())
                        .logoUrl(client.getLogo())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Client findClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));
    }

    @Override
    public ClientGetResponse getClient(Long clientId){

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));

        // Client 객체를 ClientResponse로 변환
        return ClientGetResponse.builder()
                .name(client.getName())
                .logoUrl(client.getLogo())
                .build();
    }

    private String saveLogo(MultipartFile logo) {
        // 로고 파일 저장 처리
        String directoryPath = "/logo";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            String fileName = System.currentTimeMillis() + "_" + logo.getOriginalFilename();
            Path filePath = Paths.get(directoryPath, fileName);
            Files.copy(logo.getInputStream(), filePath);
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save logo", e);
        }
    }

    private Long getAccountIdFromJwt() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return jwtProvider.getAccountId(token);
    }

    @Override
    @Transactional
    public String deleteClientById(Long clientId) {
        Long accountId = getAccountIdFromJwt(); // JWT에서 accountId 추출 (유틸리티 메서드)

        // 1. Client 존재 여부 및 권한 확인
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));

        if (!client.getAccount().getId().equals(accountId)) {
            throw new ApiException(ErrorStatus._CLIENT_FORBIDDEN);
        }

        // 2. Client 삭제
        clientRepository.delete(client);

        // 3. 성공 메시지 반환
        return "고객사 정보가 성공적으로 삭제되었습니다.";
    }

    @Override
    @Transactional
    public String updateClient(Long clientId, ClientUpdateRequest request, MultipartFile logo) {
        // 1. 고객사 조회
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + clientId));

        String logoPath;
        logoPath = (logo != null) ? s3Service.uploadFile(logo) : defaultLogoUrl;

        // 2. 수정 사항 적용
        client.updateClientInfo(request.getName(), request.getManagerName(), logoPath);

        return "고객사 정보가 성공적으로 수정되었습니다.";
    }

    @Override
    public List<ClientResponse> searchClient(String searchText) {
        // JWT에서 accountId 추출
        Long extractedAccountId = getAccountIdFromJwt();

        // 계정 검증
        Account account = accountRepository.findById(extractedAccountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND));

        // 검색 텍스트가 없으면 빈 리스트 반환
        if (searchText == null || searchText.trim().isEmpty()) {
            return List.of();
        }

        // 해당 계정의 클라이언트 중 검색
        List<Client> clients = clientRepository.findByAccountAndNameContainingIgnoreCase(account, searchText);

        // 결과 변환 및 반환
        return clients.stream()
                .map(client -> ClientResponse.builder()
                        .clientId(client.getId())
                        .name(client.getName())
                        .managerName(client.getManagerName())
                        .logoUrl(client.getLogo())
                        .build())
                .collect(Collectors.toList());
    }

}
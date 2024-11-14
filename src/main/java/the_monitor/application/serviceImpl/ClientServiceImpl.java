package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import the_monitor.application.dto.request.ClientRequest;
import the_monitor.application.dto.response.ClientResponse;
import the_monitor.application.dto.response.ReportListResponse;
import the_monitor.application.service.ClientService;
import org.springframework.stereotype.Service;
import the_monitor.application.service.S3Service;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final KeywordRepository keywordRepository;

    private final ClientMailRecipientRepository clientMailRecipientRepository;
    private final ClientMailCCRepository clientMailCCRepository;

    private final JwtProvider jwtProvider;
    private final S3Service s3Service;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, CategoryRepository categoryRepository, AccountRepository accountRepository, KeywordRepository keywordRepository,
                             ClientMailRecipientRepository clientMailRecipientRepository, ClientMailCCRepository clientMailCCRepository, JwtProvider jwtProvider, S3Service s3Service) {
        this.clientRepository = clientRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.keywordRepository = keywordRepository;
        this.clientMailRecipientRepository = clientMailRecipientRepository;
        this.clientMailCCRepository = clientMailCCRepository;
        this.jwtProvider = jwtProvider;
        this.s3Service = s3Service;
    }

    @Transactional
    public ClientResponse createClient(ClientRequest clientRequest, MultipartFile logo) {
        // JWT에서 accountId를 추출하는 과정
        Long extractedAccountId = getAccountIdFromJwt();
        Account account = accountRepository.findById(extractedAccountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String logoPath;
        logoPath = (logo != null) ? s3Service.uploadFile(logo) : "default_logo_url";

        // 클라이언트 객체 생성
        Client client = Client.builder()
                .name(clientRequest.getName())
                .managerName(clientRequest.getManagerName())
                .logo(logoPath)
                .account(account)
                .build();

        client = clientRepository.save(client);

        // 카테고리 생성 및 클라이언트에 추가
        Category category = Category.builder()
                .categoryType(clientRequest.getCategoryType())
                .client(client)
                .build();
        categoryRepository.save(category);
        client.getCategories().add(category);

        // 키워드 생성 및 저장
        List<Keyword> keywords = createKeywords(clientRequest.getIncludeKeywords(), clientRequest.getExcludeKeywords(), category);
        keywordRepository.saveAll(keywords);
        category.addKeywords(keywords);

        // 이메일 수신자와 참조인 저장
        saveEmailRecipients(clientRequest.getRecipientEmails(), clientRequest.getCcEmails(), client);

        // `keywords`를 문자열 리스트로 변환
        List<String> keywordList = keywords.stream()
                .map(Keyword::getKeyword)
                .collect(Collectors.toList());


        // ClientResponse 반환
        return ClientResponse.builder()
                .clientId(client.getId())
                .name(client.getName())
                .managerName(client.getManagerName())
                .logoUrl(client.getLogo())
                .keywords(keywordList)
                .categoryType(clientRequest.getCategoryType().toString())
                .clientMailRecipients(client.getClientMailRecipients().stream()
                        .map(ClientMailRecipient::getAddress)
                        .collect(Collectors.toList()))
                .clientMailCCs(client.getClientMailCCs().stream()
                        .map(ClientMailCC::getAddress)
                        .collect(Collectors.toList()))
                .build();
    }
    @Override
    public List<ClientResponse> getClientsByAccountId() {
        Long accountId = getAccountIdFromJwt(); // JWT에서 accountId 추출
        List<Client> clients = clientRepository.findAllByAccountId(accountId);
        if (clients.isEmpty()) {
            throw new ApiException(ErrorStatus._CLIENT_NOT_FOUND);
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

    private List<Keyword> createKeywords(List<String> includeKeywords, List<String> excludeKeywords, Category category) {
        List<Keyword> keywords = new ArrayList<>();

        // 필수 검색어 생성
        for (String keywordText : includeKeywords) {
            Keyword keyword = Keyword.builder()
                    .keyword(keywordText)
                    .keywordType(KeywordType.INCLUDE.name())
                    .category(category)
                    .build();
            keywords.add(keyword);
        }

        // 선택 검색어 생성
        for (String keywordText : excludeKeywords) {
            Keyword keyword = Keyword.builder()
                    .keyword(keywordText)
                    .keywordType(KeywordType.EXCLUDE.name())
                    .category(category)
                    .build();
            keywords.add(keyword);
        }

        return keywords;
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

    private void saveEmailRecipients(List<String> recipientEmails, List<String> ccEmails, Client client) {
        List<ClientMailRecipient> recipients = new ArrayList<>();
        for (String email : recipientEmails) {
            ClientMailRecipient recipient = ClientMailRecipient.builder()
                    .address(email)
                    .client(client)
                    .build();
            recipients.add(recipient);
        }
        clientMailRecipientRepository.saveAll(recipients);
        client.setClientMailRecipients(recipients);

        List<ClientMailCC> ccs = new ArrayList<>();
        for (String email : ccEmails) {
            ClientMailCC cc = ClientMailCC.builder()
                    .address(email)
                    .client(client)
                    .build();
            ccs.add(cc);
        }
        clientMailCCRepository.saveAll(ccs);
        client.setClientMailCCs(ccs);
    }


}
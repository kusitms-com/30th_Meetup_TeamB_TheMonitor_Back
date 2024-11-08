//package the_monitor.application.serviceImpl;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import the_monitor.application.dto.request.ClientRequest;
//import the_monitor.application.service.ClientService;
//import org.springframework.stereotype.Service;
//import the_monitor.domain.enums.KeywordType;
//import the_monitor.domain.model.ClientMailRecipient;
//import the_monitor.domain.model.Category;
//import the_monitor.domain.model.Keyword;
//import the_monitor.domain.model.Client;
//import the_monitor.domain.model.Account;
//import the_monitor.domain.repository.AccountRepository;
//import the_monitor.domain.repository.ClientRepository;
//import the_monitor.domain.repository.KeywordRepository;
//import the_monitor.domain.repository.ClientMailRecipientRepository;
//import the_monitor.infrastructure.jwt.JwtProvider;
//import io.jsonwebtoken.Claims;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class ClientServiceImpl implements ClientService {
//
//    private final ClientRepository clientRepository;
//    private final AccountRepository accountRepository;
//    private final KeywordRepository keywordRepository;
//    private final ClientMailRecipientRepository clientMailRecipientRepository;
//    private final JwtProvider jwtProvider;
//
//    @Autowired
//    public ClientServiceImpl(ClientRepository clientRepository, AccountRepository accountRepository, KeywordRepository keywordRepository, ClientMailRecipientRepository clientMailRecipientRepository, JwtProvider jwtProvider) {
//        this.clientRepository = clientRepository;
//        this.accountRepository = accountRepository;
//        this.keywordRepository = keywordRepository;
//        this.clientMailRecipientRepository = clientMailRecipientRepository;
//        this.jwtProvider = jwtProvider;
//    }
//
//    @Transactional
//    public Client createClient(ClientRequest clientRequest, Long accountId) {
//        // JWT에서 accountId를 추출
//        Long extractedAccountId = getAccountIdFromJwt();
//        Account account = accountRepository.findById(extractedAccountId)
//                .orElseThrow(() -> new RuntimeException("Account not found"));
//
//        // 로고 저장 처리
//        String logoPath = saveLogo(clientRequest.getLogo());
//
//        // 클라이언트 객체 생성
//        Client client = Client.builder()
//                .name(clientRequest.getName())
//                .managerName(clientRequest.getManagerName())
//                .logo(logoPath) // 저장된 로고 경로
//                .account(account)
//                .build();
//
//        // 카테고리 생성
//        Category category = Category.builder()
//                .categoryType(clientRequest.getCategoryType()) // 클라이언트 요청에서 카테고리 타입을 설정
//                .client(client) // 클라이언트와 연결
//                .build();
//
//        // 키워드 저장
//        List<Keyword> keywords = createKeywords(clientRequest.getIncludeKeywords(), clientRequest.getExcludeKeywords(), category); // 카테고리를 사용
//        keywordRepository.saveAll(keywords);
//
//        // 이메일 수신자 처리
//        saveEmailRecipients(clientRequest.getRecipientEmails(), client);
//
//        // 클라이언트 저장
//        return clientRepository.save(client);
//    }
//
//    private List<Keyword> createKeywords(List<String> includeKeywords, List<String> excludeKeywords, Category category) {
//        List<Keyword> keywords = new ArrayList<>();
//
//        // 필수 검색어 생성
//        for (String keywordText : includeKeywords) {
//            Keyword keyword = Keyword.builder()
//                    .keyword(keywordText)
//                    .keywordType(KeywordType.INCLUDE.name()) // 필수 검색어로 설정
//                    .category(category) // 카테고리와 연결
//                    .build();
//            keywords.add(keyword);
//        }
//
//        // 선택 검색어 생성
//        for (String keywordText : excludeKeywords) {
//            Keyword keyword = Keyword.builder()
//                    .keyword(keywordText)
//                    .keywordType(KeywordType.EXCLUDE.name()) // 선택 검색어로 설정
//                    .category(category) // 카테고리와 연결
//                    .build();
//            keywords.add(keyword);
//        }
//
//        return keywords;
//    }
//
//    private String saveLogo(MultipartFile logo) {
//        // 로고 파일 저장 처리
//        String directoryPath = "/logo"; // 여기 S3 설정하고 다시 디렉토리 설정 필요
//        File directory = new File(directoryPath);
//        if (!directory.exists()) {
//            directory.mkdirs(); // 디렉토리가 없으면 생성
//        }
//
//        // 로고 파일 저장
//        try {
//            String fileName = System.currentTimeMillis() + "_" + logo.getOriginalFilename();
//            Path filePath = Paths.get(directoryPath, fileName);
//            Files.copy(logo.getInputStream(), filePath);
//            return filePath.toString(); // 저장된 파일 경로 반환
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to save logo", e);
//        }
//    }
//
//    private Long getAccountIdFromJwt() {
//        String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
//        Claims claims = jwtProvider.parseClaims(token);
//        return claims.get("account_id", Long.class); // accountId 반환
//    }
//
//    private void saveEmailRecipients(List<String> recipientEmails, Client client) {
//        List<ClientMailRecipient> recipients = new ArrayList<>();
//        for (String email : recipientEmails) {
//            ClientMailRecipient recipient = ClientMailRecipient.builder() // 빌더 사용
//                    .address(email) // address 필드에 이메일을 할당
//                    .client(client) // 클라이언트와 연결
//                    .build();
//            recipients.add(recipient);
//        }
//        clientMailRecipientRepository.saveAll(recipients); // 수신자 이메일 저장
//    }
//}
//

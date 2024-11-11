//package the_monitor.application.serviceImpl;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.context.SecurityContextHolder;
//import the_monitor.application.dto.request.ClientRequest;
//import the_monitor.application.dto.response.ClientResponse;
//import the_monitor.application.service.ClientService;
//import org.springframework.stereotype.Service;
//import the_monitor.domain.enums.KeywordType;
//import the_monitor.domain.model.*;
//import the_monitor.domain.repository.*;
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
//import java.util.stream.Collectors;
//
//@Service
//public class ClientServiceImpl implements ClientService {
//
//    private final ClientRepository clientRepository;
//    private final CategoryRepository categoryRepository;
//    private final AccountRepository accountRepository;
//    private final KeywordRepository keywordRepository;
//    private final ClientMailRecipientRepository clientMailRecipientRepository;
//    private final ClientMailCCRepository clientMailCCRepository;
//    private final JwtProvider jwtProvider;
//
//    @Autowired
//    public ClientServiceImpl(ClientRepository clientRepository, CategoryRepository categoryRepository, AccountRepository accountRepository, KeywordRepository keywordRepository, ClientMailRecipientRepository clientMailRecipientRepository, ClientMailCCRepository clientMailCCRepository, JwtProvider jwtProvider) {
//        this.clientRepository = clientRepository;
//        this.categoryRepository = categoryRepository;
//        this.accountRepository = accountRepository;
//        this.keywordRepository = keywordRepository;
//        this.clientMailRecipientRepository = clientMailRecipientRepository;
//        this.clientMailCCRepository = clientMailCCRepository;
//        this.jwtProvider = jwtProvider;
//    }
//
//    @Transactional
//    public Client createClient(ClientRequest clientRequest, MultipartFile logo) {
//        // JWT에서 accountId를 추출
//        Long extractedAccountId = getAccountIdFromJwt();
//        Account account = accountRepository.findById(extractedAccountId)
//                .orElseThrow(() -> new RuntimeException("Account not found"));
//
//        String logoPath = (logo != null) ? saveLogo(logo) : "default_logo_url"; // 기본 이미지 URL 설정
//
//        // 클라이언트 객체 생성
//        Client client = Client.builder()
//                .name(clientRequest.getName())
//                .managerName(clientRequest.getManagerName())
//                .logo(logoPath) // 저장된 로고 경로
//                .account(account)
//                .build();
//
//        client = clientRepository.save(client);
//
//        // ClientMailRecipients 주소 리스트 생성
//        List<String> clientMailRecipientAddresses = client.getClientMailRecipients().stream()
//                .map(ClientMailRecipient::getAddress)
//                .collect(Collectors.toList());
//
//        // ClientMailCCs 주소 리스트 생성
//        List<String> clientMailCCAddresses = client.getClientMailCCs().stream()
//                .map(ClientMailCC::getAddress)
//                .collect(Collectors.toList());
//
//        // 카테고리 생성
//        Category category = Category.builder()
//                .categoryType(clientRequest.getCategoryType()) // 클라이언트 요청에서 카테고리 타입을 설정
//                .client(client) // 클라이언트와 연결
//                .build();
//
//        categoryRepository.save(category);
//        client.setCategory(category);
//
//        // 키워드 저장
//        List<Keyword> keywords = createKeywords(clientRequest.getIncludeKeywords(), clientRequest.getExcludeKeywords(), category); // 카테고리를 사용
//        keywordRepository.saveAll(keywords);
//        category.addKeywords(keywords);;
//
//        saveEmailRecipients(clientRequest.getRecipientEmails(), clientRequest.getCcEmails(), client);
//
//
//        // 클라이언트 저장
//        return ClientResponse.builder()
//                .clientId(client.getId())
//                .name(client.getName())
//                .managerName(client.getManagerName())
//                .logoUrl(client.getLogo())
//                .categoryType(clientRequest.getCategoryType().toString()) // 클라이언트가 선택한 카테고리 타입을 사용
//                .clientMailRecipients(clientMailRecipientAddresses)
//                .clientMailCCs(clientMailCCAddresses)
//                .build();
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
//        return jwtProvider.getAccountId(token);
//    }
//
//    private void saveEmailRecipients(List<String> recipientEmails, List<String> ccEmails, Client client) {
//        List<ClientMailRecipient> recipients = new ArrayList<>();
//        for (String email : recipientEmails) {
//            ClientMailRecipient recipient = ClientMailRecipient.builder()
//                    .address(email)
//                    .client(client)
//                    .build();
//            recipients.add(recipient);
//        }
//        clientMailRecipientRepository.saveAll(recipients); // 수신자 이메일 저장
//        client.setClientMailRecipients(recipients); // 클라이언트에 수신자 설정
//
//        List<ClientMailCC> ccs = new ArrayList<>();
//        for (String email : ccEmails) {
//            ClientMailCC cc = ClientMailCC.builder()
//                    .address(email)
//                    .client(client)
//                    .build();
//            ccs.add(cc);
//        }
//        clientMailCCRepository.saveAll(ccs); // 참조인 이메일 저장
//        client.setClientMailCCs(ccs); // 클라이언트에 참조인 설정
//    }
//
//}
//
package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import the_monitor.application.dto.request.ClientRequest;
import the_monitor.application.dto.response.ClientResponse;
import the_monitor.application.service.ClientService;
import org.springframework.stereotype.Service;
import the_monitor.application.service.S3Service;
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
        // JWT에서 accountId를 추출
        Long extractedAccountId = getAccountIdFromJwt();
        Account account = accountRepository.findById(extractedAccountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String logoPath;
        try {
            logoPath = (logo != null) ? s3Service.uploadFile(logo) : "default_logo_url";
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload logo to S3", e);
        }

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

        // ClientResponse 반환
        return ClientResponse.builder()
                .clientId(client.getId())
                .name(client.getName())
                .managerName(client.getManagerName())
                .logoUrl(client.getLogo())
                .categoryType(clientRequest.getCategoryType().toString())
                .clientMailRecipients(client.getClientMailRecipients().stream()
                        .map(ClientMailRecipient::getAddress)
                        .collect(Collectors.toList()))
                .clientMailCCs(client.getClientMailCCs().stream()
                        .map(ClientMailCC::getAddress)
                        .collect(Collectors.toList()))
                .build();
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

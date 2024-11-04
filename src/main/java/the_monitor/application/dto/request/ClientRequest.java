package the_monitor.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.enums.KeywordType;

import java.util.List;

@Getter
@NoArgsConstructor
public class ClientRequest {
    @NotBlank(message = "클라이언트 이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "담당자 이름은 필수 항목입니다.")
    private String managerName;

    private MultipartFile logo; // 로고는 선택 사항

    @NotNull(message = "카테고리 타입은 필수 항목입니다.")
    private CategoryType categoryType; // SELF, COMPETITOR, INDUSTRY 중 하나

    @NotEmpty(message = "포함할 검색 키워드는 필수 항목입니다.")
    private List<String> includeKeywords; // 필수 검색어 리스트

    private List<String> excludeKeywords; // 제외 검색어 리스트 (선택 사항)

    @NotEmpty(message = "메일 수신자는 필수 항목입니다.")
    private List<String> recipientEmails; // 수신자 이메일 (필수)

    private List<String> ccEmails; // 참조인 이메일 (선택)


}

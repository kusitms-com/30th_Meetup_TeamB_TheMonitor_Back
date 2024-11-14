package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class ClientResponse {

    private Long clientId;
    private String name;
    private String managerName;
    private String logoUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> keywords;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String categoryType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> clientMailRecipients; // 수신자 주소만 포함

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> clientMailCCs; // 참조인 주소만 포함

    @Builder
    public ClientResponse(Long clientId,
                          String name,
                          String managerName,
                          String logoUrl,
                          List<String> keywords,
                          String categoryType,
                          List<String> clientMailRecipients,
                          List<String> clientMailCCs) {

        this.clientId = clientId;
        this.name = name;
        this.managerName = managerName;
        this.logoUrl = logoUrl;
        this.keywords = keywords;
        this.categoryType = categoryType;
        this.clientMailRecipients = clientMailRecipients;
        this.clientMailCCs = clientMailCCs;
    }
}

package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class EmailResponse {
    private List<String> recipients;
    private List<String> ccs;

    @Builder
    public EmailResponse(List<String> recipients, List<String> ccs) {
        this.recipients = recipients;
        this.ccs = ccs;
    }
}

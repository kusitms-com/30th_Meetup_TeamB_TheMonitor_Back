package the_monitor.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ArticleRequest {

    @NotBlank(message = "page 번호는 필수입니다.")
    private int page;

    @NotBlank(message = "page 당 보여질 갯수는 필수입니다.")
    private int size;

}

package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.ClientRequest;
import the_monitor.application.dto.response.ClientResponse;
import the_monitor.application.service.ClientService;
import the_monitor.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "고객사 정보 입력", description = "고객사에 대한 정보를 입력합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(
            @RequestPart(name = "clientRequest") @Valid ClientRequest request,
            @RequestPart(name = "logo", required = false) MultipartFile logo) {

        ClientResponse clientResponse = clientService.createClient(request, logo);
        ApiResponse<ClientResponse> response = ApiResponse.onSuccessData("클라이언트 생성 성공", clientResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "고객사 정보 조회", description = "로그인한 유저의 accountId로 고객사 정보를 조회합니다.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<ClientResponse>> getClientInfo()     {
        List<ClientResponse> clientResponses = clientService.getClientsByAccountId();
        return ApiResponse.onSuccessData("클라이언트 조회 성공", clientResponses);
    }

}
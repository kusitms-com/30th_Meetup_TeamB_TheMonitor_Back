package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.ClientRequest;
import the_monitor.application.dto.request.ClientUpdateRequest;
import the_monitor.application.dto.request.ReportUpdateTitleRequest;
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
    public ApiResponse<List<ClientResponse>> getClient()     {
        List<ClientResponse> clientResponses = clientService.getClientsByAccountId();
        return ApiResponse.onSuccessData("클라이언트 조회 성공", clientResponses);
    }

    @Operation(summary = "고객사 정보 삭제", description = "고객사 정보를 삭제합니다.")
    @DeleteMapping()
    public ApiResponse<String> deleteClient(@RequestParam("clientId") Long clientId) {
        return ApiResponse.onSuccess(clientService.deleteClientById(clientId));

    }

    @Operation(summary = "clietId로 고객사 정보 반환", description = "clientId로 고객사 정보를 조회합니다.")
    @GetMapping("/info")
    public ApiResponse<ClientResponse> getClientInfo(@RequestParam("clientId") Long clientId){
        return ApiResponse.onSuccessData("클라이언트 정보 조회 성공", clientService.getClient(clientId));
    }

    @Operation(summary = "고객사 정보 수정", description = "고객사 정보를 수정합니다.")
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> UpdateClient(@RequestParam("clientId") Long clientId,
                                            @RequestPart("clientData") @Valid ClientUpdateRequest request,
                                            @RequestPart(name = "logo", required = false) MultipartFile logo) {
        return ApiResponse.onSuccess(clientService.updateClient(clientId, request, logo));

    }

}
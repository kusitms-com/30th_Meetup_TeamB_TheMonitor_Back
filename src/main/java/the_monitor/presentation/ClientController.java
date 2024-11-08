//package the_monitor.presentation;
//
//import jakarta.validation.Valid;
//import the_monitor.application.dto.request.ClientRequest;
//import the_monitor.application.service.ClientService;
//import the_monitor.common.ApiResponse;
//import the_monitor.domain.model.Client;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//@RestController
//@RequestMapping("/api/clients")
//@RequiredArgsConstructor
//public class ClientController {
//
//    private final ClientService clientService;
//
//    @PostMapping("/{accountId}")
//    public ResponseEntity<Client> createClient(@PathVariable Long accountId,
//            @RequestBody @Valid ClientRequest request) {
//
//        clientService.createClient(request, accountId);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .build();
//    }
//}
//package the_monitor.presentation;
//
//import io.swagger.v3.oas.annotations.Operation;
//import jakarta.validation.Valid;
//import the_monitor.application.dto.request.ClientRequest;
//import the_monitor.application.service.ClientService;
//import the_monitor.common.ApiResponse;
//import the_monitor.domain.model.Client;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/clients")
//@RequiredArgsConstructor
//public class ClientController {
//
//    private final ClientService clientService;
//
//    @Operation(summary = "고객사 정보 입력", description = "고객사에 대한 정보를 입력합니다.")
//    @PostMapping("/{accountId}")
//    public ResponseEntity<ApiResponse<Client>> createClient(
//                                               @RequestBody @Valid ClientRequest request) {
//        Client client = clientService.createClient(request);
//        ApiResponse<Client> response = ApiResponse.onSuccessData("클라이언트 생성 성공", client);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//}






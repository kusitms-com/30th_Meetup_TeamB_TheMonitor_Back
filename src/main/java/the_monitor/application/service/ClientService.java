package the_monitor.application.service;

import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.ClientRequest;
import the_monitor.application.dto.response.ClientResponse;
import the_monitor.domain.model.Client;

public interface ClientService {
    ClientResponse createClient(ClientRequest request, MultipartFile logo);
}

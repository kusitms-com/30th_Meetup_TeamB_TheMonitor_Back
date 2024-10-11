package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.request.AccountCreateRequest;
import the_monitor.application.service.AccountService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.model.Account;
import the_monitor.domain.repository.AccountRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND));
    }

    @Override
    @Transactional
    public String createAccount(AccountCreateRequest request) {

        accountRepository.save(request.toEntity());
        return "계정 생성 완료";

    }

//    @Override
//    public void registerUser() {
//
//        User user = new User();
//        user.setUsername(signupRequest.getUsername());
//        user.setEmail(signupRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
//        userRepository.save(user);
//
//    }

}

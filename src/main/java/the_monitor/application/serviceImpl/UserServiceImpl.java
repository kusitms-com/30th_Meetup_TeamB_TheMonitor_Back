package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.service.UserService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.model.User;
import the_monitor.domain.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorStatus._USER_NOT_FOUND));
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

package the_monitor.application.service;

import the_monitor.domain.model.User;

public interface UserService {

//    void registerUser();
    User findUserById(Long id);
}

package the_monitor.application.serviceImpl;

import org.springframework.stereotype.Service;
import the_monitor.application.service.TemporaryPasswordGenerateService;

import java.security.SecureRandom;

@Service
public class TemporaryPasswordGenerateServiceImpl implements TemporaryPasswordGenerateService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int PASSWORD_LENGTH = 12;

    public String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

}

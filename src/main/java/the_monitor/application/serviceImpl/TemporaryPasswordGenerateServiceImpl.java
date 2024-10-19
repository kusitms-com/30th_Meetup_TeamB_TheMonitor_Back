package the_monitor.application.serviceImpl;

import org.springframework.stereotype.Service;
import the_monitor.application.service.TemporaryPasswordGenerateService;

import java.security.SecureRandom;

@Service
public class TemporaryPasswordGenerateServiceImpl implements TemporaryPasswordGenerateService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%";
    private static final int PASSWORD_LENGTH = 12;

    public String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // 특수문자 하나 무작위 선택
        int specialCharIndex = random.nextInt(SPECIAL_CHARACTERS.length());
        char specialChar = SPECIAL_CHARACTERS.charAt(specialCharIndex);

        // 특수문자 삽입 위치 무작위 선택
        int randomPosition = random.nextInt(PASSWORD_LENGTH);
        password.insert(randomPosition, specialChar);

        // 나머지 문자 무작위 선택
        for (int i = 0; i < PASSWORD_LENGTH - 1; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.insert(i < randomPosition ? i : i + 1, CHARACTERS.charAt(index)); // 특수문자 삽입 위치보다 큰 위치에는 1을 더해줌
        }

        return password.toString();
    }
}
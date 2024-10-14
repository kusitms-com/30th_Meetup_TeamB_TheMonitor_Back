package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import the_monitor.application.service.CertifiedKeyService;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertifiedKeyServiceImpl implements CertifiedKeyService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String generateCertifiedKey() {
        // 인증 코드 생성
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            key.append((int) (Math.random() * 10));
        }
        return key.toString();
    }

    @Override
    public void saveCertifiedKey(String email, String key) {
        // 인증 코드를 Redis에 저장, 10분 후 자동 삭제
        redisTemplate.opsForValue().set(email, key, 3, TimeUnit.MINUTES);
    }

    @Override
    public String getCertifiedKey(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }

    @Override
    public void deleteCertifiedKey(String email) {
        redisTemplate.delete(email);
    }


}

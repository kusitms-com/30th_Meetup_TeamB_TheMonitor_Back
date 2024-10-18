package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import the_monitor.application.service.CertifiedKeyService;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertifiedKeyServiceImpl implements CertifiedKeyService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String generateCertifiedKey() {
        Random random = new SecureRandom();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));  // 0-9 사이의 숫자를 생성
        }
        return key.toString();
    }

    @Override
    public boolean existsCertifiedKey(String email) {
        // Redis에 해당 키가 존재하는지 확인
        return Boolean.TRUE.equals(redisTemplate.hasKey(email));
    }

    @Override
    public void saveCertifiedKey(String email, String key) {

        // 인증 코드를 Redis에 저장, 10분 후 자동 삭제
        redisTemplate.opsForValue().set(email, key, 10, TimeUnit.MINUTES);

    }

    @Override
    public String getCertifiedKey(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }

    @Override
    public void deleteCertifiedKey(String email) {
        redisTemplate.delete(email);
    }

    // 인증 코드가 존재하는지 확인하는 메서드 추가
    @Override
    public boolean isCertifiedKeyExpired(String email) {
        Long ttl = redisTemplate.getExpire(email, TimeUnit.SECONDS);

        if (ttl == null || ttl == -2) {
            // 키가 존재하지 않음 (이미 삭제됨)
            return true;
        }

        // 남은 TTL이 420초 미만이면 3분이 지난 것으로 간주하여 만료 처리
        return ttl < 420;

    }

}

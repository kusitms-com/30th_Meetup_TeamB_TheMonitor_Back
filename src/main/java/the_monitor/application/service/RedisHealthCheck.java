package the_monitor.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisHealthCheck {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void testRedisConnection() {
        try {
            redisTemplate.opsForValue().set("testKey", "testValue");
            String value = redisTemplate.opsForValue().get("testKey");
            if ("testValue".equals(value)) {
                System.out.println("Redis connection successful");
            } else {
                System.out.println("Redis connection failed");
            }
        } catch (Exception e) {
            System.out.println("Failed to connect to Redis: " + e.getMessage());
        }
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate2;

    public String checkConnection() {
        try {
            // Ping 명령어 실행
            String pingResponse = redisTemplate2.getConnectionFactory().getConnection().ping();
            return "Redis 연결 상태: " + pingResponse; // "PONG" 응답이면 연결 정상
        } catch (Exception e) {
            return "Redis 연결 실패: " + e.getMessage();
        }
    }

}

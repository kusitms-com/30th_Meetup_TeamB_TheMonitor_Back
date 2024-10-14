package the_monitor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import the_monitor.application.service.RedisHealthCheck;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing  // Auditing 기능을 활성화
@RequiredArgsConstructor
public class TheMonitorApplication {

    private final RedisHealthCheck redisHealthCheck;

    public static void main(String[] args) {

        SpringApplication.run(TheMonitorApplication.class, args);

    }

    // 애플리케이션 시작 후 Redis 연결 확인
    @PostConstruct
    public void checkRedisConnection() {
        redisHealthCheck.testRedisConnection();

        log.info(redisHealthCheck.checkConnection());
    }

}

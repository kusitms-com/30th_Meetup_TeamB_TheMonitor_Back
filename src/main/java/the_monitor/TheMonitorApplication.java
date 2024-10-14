package the_monitor;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing  // Auditing 기능을 활성화
public class TheMonitorApplication {

    @Value("${spring.redis.host}")
    private String redisHost;

    @PostConstruct
    public void logRedisHost() {
        log.info("Redis host: " + redisHost);
    }

    public static void main(String[] args) {

        SpringApplication.run(TheMonitorApplication.class, args);

    }

}

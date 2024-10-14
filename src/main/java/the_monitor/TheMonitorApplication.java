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

    public static void main(String[] args) {

        SpringApplication.run(TheMonitorApplication.class, args);

    }

}

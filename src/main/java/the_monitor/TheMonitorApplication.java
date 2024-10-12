package the_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // Auditing 기능을 활성화
public class TheMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheMonitorApplication.class, args);
    }

}

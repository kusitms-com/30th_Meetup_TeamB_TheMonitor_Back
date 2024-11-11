package the_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableJpaRepositories(basePackages = "the_monitor.domain.repository")
public class TheMonitorApplication {

    public static void main(String[] args) {

        SpringApplication.run(TheMonitorApplication.class, args);

    }

}

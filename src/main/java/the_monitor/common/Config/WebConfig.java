package the_monitor.common.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // 허용할 도메인
                .allowedMethods("*") // 모든 HTTP 메소드 허용
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 자격 증명 허용
                .maxAge(3600); // 3600초 동안 캐시

        // Swagger 관련 경로 허용
        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("*") // Swagger UI 접근 허용
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600); // 3600초 동안 캐시

        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600); // 3600초 동안 캐시

        registry.addMapping("/swagger-resources/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600); // 3600초 동안 캐시
    }

}
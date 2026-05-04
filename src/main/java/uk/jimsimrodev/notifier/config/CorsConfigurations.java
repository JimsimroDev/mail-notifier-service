package uk.jimsimrodev.notifier.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfigurations implements WebMvcConfigurer {

    @Value("${cors-settings.url}")
    private String urlCors;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(urlCors)
                .allowedMethods("POST");

        registry.addMapping("/api/v1/health").allowedOrigins(urlCors)
                .allowedMethods("GET");
    }
}

package org.finance.financemanager.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("financemanager.cors")
public class CorsConfig {
    private List<String> allowedOrigins;
    private List<String> allowedHeaders = List.of("*");
    private List<String> allowedMethods = List.of("*");
    private Boolean allowCredentials = true;
    private Long maxAge = 3600L;
}

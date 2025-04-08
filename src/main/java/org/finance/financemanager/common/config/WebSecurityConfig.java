package org.finance.financemanager.common.config;

import lombok.RequiredArgsConstructor;
import org.finance.financemanager.accessibility.roles.entities.RoleName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CorsConfig corsConfig;
    private final FirebaseJwtFilter firebaseJwtFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(corsConfig.getAllowedOrigins());
        config.setAllowedMethods(corsConfig.getAllowedMethods());
        config.setAllowedHeaders(corsConfig.getAllowedHeaders());
        config.setAllowCredentials(corsConfig.getAllowCredentials());
        config.setMaxAge(corsConfig.getMaxAge());
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(config -> corsConfigurationSource())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/public/**", "/actuator/**").permitAll()
                        .requestMatchers("/common/**").authenticated()
                        .requestMatchers("/client/**").hasAuthority(RoleName.CLIENT.name())
                        .requestMatchers("/admin/**").hasAuthority(RoleName.ADMIN.name())
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .addFilterBefore(firebaseJwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

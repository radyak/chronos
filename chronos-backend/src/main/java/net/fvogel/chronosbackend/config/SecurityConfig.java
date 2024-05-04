package net.fvogel.chronosbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!no-security")
public class SecurityConfig {

    @Autowired
    KeycloakJwtAuthConverter keycloakJwtAuthConverter;

    @Value("${app.auth.admin-role}")
    String adminRole;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(Customizer.withDefaults());
        http.authorizeHttpRequests(
                httpRequests -> httpRequests
                        .requestMatchers("/api/admin/**").hasRole(adminRole)
                        .anyRequest().anonymous()
        );
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .jwt(jwt -> jwt
                        .jwtAuthenticationConverter(keycloakJwtAuthConverter)
                )
        );
        return http.build();
    }
}

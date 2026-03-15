package net.fvogel.chronos.data.config.security;

import net.fvogel.chronos.commons.security.ChronosJwtAuthConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!no-security")
public class SecurityConfig {

    @Autowired
    ChronosJwtAuthConverter jwtAuthConverter;

    @Value("${app.auth.admin-role}")
    String adminRole;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Default CORS
        http.cors(Customizer.withDefaults());

        // Disable CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // Protect
        http.authorizeHttpRequests(
                httpRequests -> httpRequests
                        .requestMatchers("/api/admin/**").hasRole(adminRole)
                        .anyRequest().anonymous()
        );

        // Stateless session mgmt
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Enable JWT Auth
        http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthConverter)
                )
        );

        return http.build();
    }
}

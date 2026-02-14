package net.fvogel.chronos.schema.config.security;

import net.fvogel.chronos.commons.security.ChronosJwtAuthConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({"!no-security"})
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    ChronosJwtAuthConverter jwtAuthConverter;

    @Autowired
    JwtDecoder jwtDecoder;

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
                auth -> auth
                        .requestMatchers("/api/schema/admin/**").hasRole(adminRole)
                        .anyRequest().permitAll()
        );

        // Stateless session mgmt
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Enable JWT Auth
        http.oauth2ResourceServer(config -> config
                .jwt(
                        jwt -> {
                            jwt.decoder(jwtDecoder);
                            jwt.jwtAuthenticationConverter(jwtAuthConverter);
                        }
                )
        );

        return http.build();
    }
}

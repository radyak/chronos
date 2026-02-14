package net.fvogel.chronos.commons.security;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generates JWTs for development, encoded/signed by dev JWT encoder (using own public/private key pair;
 * see DevSecurityConfig).
 */
@Component
@Profile({"test", "dev"})
public class DevJwtGenerator {

    Logger logger = LoggerFactory.getLogger(DevJwtGenerator.class);

    @Value("${app.auth.admin-role}")
    String adminRole;

    @Value("${app.auth.client-id}")
    String clientId;

    @Value("${app.auth.principle-attribute}")
    String principleAttribute;

    @Autowired
    JwtEncoder jwtEncoder;

    @PostConstruct
    public void postConstruct() {
        logger.info("JWT for 'user' without any roles: {}", generateJWT("user"));
        logger.info("JWT for 'admin-user' with role '{}': {}", adminRole, generateJWT("role-user", Set.of(adminRole)));
    }

    public String generateJWT(String userName) {
        return generateJWT(userName, Set.of());
    }

    public String generateJWT(String userName, Set<String> roles) {
        var now = Instant.now();
        // ~100 years valid
        var exp = now.plusSeconds((long) 60 * 60 * 24 * 365 * 100);

        var resourceAccess = new HashMap<String, Map<String, Set<String>>>();
        var clientProperties = new HashMap<String, Set<String>>();
        clientProperties.put("roles", roles);
        resourceAccess.put(clientId, clientProperties);

        var claims = JwtClaimsSet.builder()
                .issuer("localhost")
                .subject(userName)
                .issuedAt(now)
                .expiresAt(exp)
                .claim("resource_access", resourceAccess)
                .claim(principleAttribute, userName)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}

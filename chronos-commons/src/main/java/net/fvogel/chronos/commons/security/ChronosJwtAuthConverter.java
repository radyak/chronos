package net.fvogel.chronos.commons.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Chronos Standard JWT Converter.
 * <p>
 * Converts a JWT with an expected structure for user principles and roles.
 * Besides standard claims like exp, iss etc. it expects this custom structure:
 * {
 * ...
 * "resource_access": {
 * "${app.auth.client-id}": {
 * "roles": [
 * "app-roles" <- This is where backends expect their respective role(s)
 * ]
 * },
 * ...
 * },
 * "${app.auth.principle-attribute}": "my User name",
 * ...
 * }
 */
@Component
public class ChronosJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Logger logger = LoggerFactory.getLogger(ChronosJwtAuthConverter.class);

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Value("${app.auth.principle-attribute}")
    private String principleAttribute;

    @Value("${app.auth.client-id}")
    private String clientId;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                getPrincipleClaimName(jwt)
        );
    }

    private String getPrincipleClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (principleAttribute != null) {
            claimName = principleAttribute;
        }
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        if (!jwt.hasClaim("resource_access")) {
            logger.debug("Claim 'resource_access' not present; not authorized");
            return Set.of();
        }
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (!resourceAccess.containsKey(clientId)) {
            logger.debug("Client ID '" + clientId + "' not present in 'resource_access'; not authorized");
            return Set.of();
        }
        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(clientId);

        Collection<String> resourceRoles = (Collection<String>) resource.get("roles");
        return resourceRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}

package net.fvogel.chronos.commons.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Provides JWT en- and decoder for development, which use an own public/private key pair.
 */
@Configuration
@Profile({"test", "dev"})
public class DevSecurityConfig {

    @Value("classpath:dev-jwt-private.key")
    private RSAPrivateKey privateKey;

    @Value("classpath:dev-jwt-public.key")
    private RSAPublicKey publicKey;

    @Bean
    JwtEncoder jwtEncoder() {
        var jwk = new RSAKey.Builder(this.publicKey)
                .privateKey(this.privateKey)
                .build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }
}

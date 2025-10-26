package net.fvogel.chronosbackend.general.webconfig.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("app.auth")
public class AuthConfig {

    private String issuer;
    private String clientId;

}


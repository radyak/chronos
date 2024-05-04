package net.fvogel.chronosbackend.open.rest;

import lombok.Data;
import net.fvogel.chronosbackend.common.model.AuthConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config/web")
public class WebConfigController {

    private AuthConfig authConfig;

    public WebConfigController(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @GetMapping(value = "/app-config.json", produces = "application/json")
    public WebAppConfig webAppConfig(
    ) {
        return new WebAppConfig(authConfig.getIssuer(), authConfig.getClientId());
    }

    private record WebAppConfig(
            String issuer,
            String clientId) {}
}

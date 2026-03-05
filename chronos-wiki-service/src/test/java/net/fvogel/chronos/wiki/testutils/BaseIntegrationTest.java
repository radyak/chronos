package net.fvogel.chronos.wiki.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronos.commons.security.TestJwtGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.cache.type=NONE"})
public abstract class BaseIntegrationTest {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${app.auth.admin-role}")
    protected String adminRole;
    @Autowired
    protected TestJwtGenerator jwtGenerator;
    @Autowired
    protected WebApplicationContext context;
    protected MockMvc mvc;

    @BeforeEach
    public void setup() throws IOException {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @AfterEach
    public void tearDown() {
    }

    protected String adminAuthHeader() {
        return authHeader("admin", Set.of(adminRole));
    }

    protected String authHeader(String username) {
        return authHeader(username, Set.of());
    }

    protected String authHeader(String username, Set<String> roles) {
        return "Bearer " + jwtGenerator.generateJWT(username, roles);
    }

    protected ResultActions getEntity(String entityKey) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get("/api/schema/{key}", entityKey));
    }

}

package net.fvogel.chronosbackend.it.admin;

import net.fvogel.chronosbackend.commons.security.DevJwtGenerator;
import net.fvogel.chronosbackend.domain.schema.persistence.repository.EntityPORepository;
import net.fvogel.chronosbackend.shared.dev.TestDataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AdminApiSecurityIntegrationTest {

    @Value("${app.auth.admin-role}")
    String adminRole;

    @Autowired
    DevJwtGenerator jwtGenerator;
    @Autowired
    EntityPORepository entityPORepository;
    @Autowired
    private TestDataManager testDataManager;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    public void setup() throws IOException {
        testDataManager.importTestData();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @AfterEach
    public void tearDown() {
        testDataManager.clearAll();
    }

    @Test
    void unauthenticatedUserCannotDeleteEntity() throws Exception {
        assertThat(entityPORepository.findByKey("Territory").isPresent());
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory"))
                .andExpect(status().isUnauthorized());
        assertThat(entityPORepository.findByKey("Territory").isPresent());
    }

    @Test
    void unauthorizedUserCannotDeleteEntity() throws Exception {
        assertThat(entityPORepository.findByKey("Territory").isPresent());
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory")
                .header("Authorization", "Bearer " + jwtGenerator.generateJWT("admin", Set.of()))
        ).andExpect(status().isForbidden());
        assertThat(entityPORepository.findByKey("Territory").isPresent());
    }

    @Test
    void adminRoleAuthorizedUserCanDeleteEntity() throws Exception {
        assertThat(entityPORepository.findByKey("Territory").isPresent());
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory")
                .header("Authorization", "Bearer " + jwtGenerator.generateJWT("admin", Set.of(adminRole)))
        ).andExpect(status().isOk());
        assertThat(entityPORepository.findByKey("Territory").isEmpty());
    }

}

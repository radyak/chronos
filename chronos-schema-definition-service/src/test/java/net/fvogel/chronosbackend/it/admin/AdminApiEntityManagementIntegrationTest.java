package net.fvogel.chronosbackend.it.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronosbackend.commons.security.DevJwtGenerator;
import net.fvogel.chronosbackend.domain.schema.persistence.repository.EntityPORepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static net.fvogel.chronosbackend.testutils.EntityBuilder.createMinimalEntity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "test-data"})
public class AdminApiEntityManagementIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${app.auth.admin-role}")
    String adminRole;
    @Autowired
    DevJwtGenerator jwtGenerator;
    @Autowired
    EntityPORepository entityPORepository;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void createEntity() throws Exception {
        mvc.perform(post("/api/schema/admin/entities")
                .content(objectMapper.writeValueAsString(createMinimalEntity("Dynasty")))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void cannotCreateDuplicateKeyEntity() throws Exception {
        mvc.perform(post("/api/schema/admin/entities")
                .content(objectMapper.writeValueAsString(createMinimalEntity("Territory")))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    private String adminAuthHeader() {
        return "Bearer " + jwtGenerator.generateJWT("admin", Set.of(adminRole));
    }

}

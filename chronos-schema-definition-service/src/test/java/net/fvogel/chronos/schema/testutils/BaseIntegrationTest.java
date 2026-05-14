package net.fvogel.chronos.schema.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronos.commons.security.TestJwtGenerator;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.TypePORepository;
import net.fvogel.chronos.schema.shared.dev.TestDataManager;
import org.hibernate.Hibernate;
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
    protected TestDataManager testDataManager;
    @Autowired
    protected WebApplicationContext context;
    @Autowired
    protected TypePORepository typePORepository;
    protected MockMvc mvc;

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

    protected String adminAuthHeader() {
        return authHeader("admin", Set.of(adminRole));
    }

    protected String authHeader(String username) {
        return authHeader(username, Set.of());
    }

    protected String authHeader(String username, Set<String> roles) {
        return "Bearer " + jwtGenerator.generateJWT(username, roles);
    }

    protected ResultActions getType(String typeKey) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get("/api/schema/{key}", typeKey));
    }

    protected TypePO loadType(String typeKey) {
        TypePO type = typePORepository.findByKey(typeKey).get();
        Hibernate.initialize(type);
        type.getRelations().forEach(relation -> {
            relation.setSource(null);
            TypePO newTarget = new TypePO();
            newTarget.setId(relation.getTarget().getId());
            relation.setTarget(newTarget);
        });

        return type;
    }

}

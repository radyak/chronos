package net.fvogel.chronosbackend.it;

import net.fvogel.chronosbackend.shared.dev.TestDataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PublicApiIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TestDataManager testDataManager;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() throws IOException {
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
    void getCompleteSchema() throws Exception {
        mvc.perform(get("/api/schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("*"))

                .andExpect(jsonPath("$.entities.elements.length()").value(2))
                .andExpect(jsonPath("$.entities.defaultAttributes.length()").value(3))

                .andExpect(jsonPath("$.relations.elements.length()").value(2))
                .andExpect(jsonPath("$.relations.defaultAttributes.length()").value(3));
    }

    @Test
    void getSingleEntity() throws Exception {
        mvc.perform(get("/api/schema/{key}", "Territory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("Territory"))

                .andExpect(jsonPath("$.entities.elements.length()").value(2))
                .andExpect(jsonPath("$.entities.defaultAttributes.length()").value(3))

                .andExpect(jsonPath("$.relations.elements.length()").value(1))
                .andExpect(jsonPath("$.relations.defaultAttributes.length()").value(3));
    }

    @Test
    void cannotGetUnknownEntity() throws Exception {
        mvc.perform(get("/api/schema/{key}", "Dynasty"))
                .andExpect(status().isNotFound());
    }

}

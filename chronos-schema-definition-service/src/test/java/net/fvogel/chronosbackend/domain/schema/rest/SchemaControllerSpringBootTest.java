package net.fvogel.chronosbackend.domain.schema.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-data")
public class SchemaControllerSpringBootTest {

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
    void getEntity() throws Exception {
        mvc.perform(get("/api/schema/{key}", "Territory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("Territory"))
                .andExpect(jsonPath("$.entities.elements.length()").value(2));
    }

    @Test
    void cannotGetUnknownEntity() throws Exception {
        mvc.perform(get("/api/schema/{key}", "Dynasty"))
                .andExpect(status().isNotFound());
    }

}

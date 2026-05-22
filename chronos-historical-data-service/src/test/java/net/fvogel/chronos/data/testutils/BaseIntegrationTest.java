package net.fvogel.chronos.data.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronos.data.dev.TestDataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    protected WebApplicationContext context;
    protected MockMvc mvc;
    @Autowired
    TestDataManager testDataManager;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        testDataManager.importTestData();
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
//                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @AfterEach
    public void tearDown() throws IOException {
        testDataManager.clearAll();
    }

}

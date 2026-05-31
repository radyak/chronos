package net.fvogel.chronos.data.it.bykey;

import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiGetByKeyIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void getDataByKeyReturnsMatchingEntry() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/vespasian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.properties.name").value("Titus Flavius Vespasianus"));
    }

    @Test
    void getDataThrowsNotFoundForUnknownKey() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/unknown"))
                .andExpect(status().isNotFound());
    }
}

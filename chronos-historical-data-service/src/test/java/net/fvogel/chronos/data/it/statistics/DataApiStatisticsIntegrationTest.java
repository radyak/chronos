package net.fvogel.chronos.data.it.statistics;

import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiStatisticsIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void getStatisticsReturnsStatistics() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].label").value("Territory"))
                .andExpect(jsonPath("$.[0].count").value(3))
                .andExpect(jsonPath("$.[1].label").value("Person"))
                .andExpect(jsonPath("$.[1].count").value(20));
    }

}

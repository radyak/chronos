package net.fvogel.chronos.data.it.mesh;

import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static net.fvogel.chronos.data.testutils.DataApiRequestMatcher.toExactlyContainKeys;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiMeshFilteringIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void getDataWithFilterParamReturnsMatchingEntry() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?wikiqid=Q1416"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(1))
                .andExpect(toExactlyContainKeys("otho"));
    }

    @Test
    void getDataWithNonmatchingFilterParamReturnsNoEntry() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?wikiqid=DOESNOTEXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(0));
    }

    @Test
    void getDataWithGreaterThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?start:gt=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(13));
    }

    @Test
    void getDataWithGreaterEqualsThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?start:gte=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(14));
    }

    @Test
    void getDataWithLessThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?start:lt=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(2))
                .andExpect(toExactlyContainKeys(
                        "vespasian",
                        "vitellius"
                ));
    }

    @Test
    void getDataWithLessEqualsThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?start:lte=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(3))
                .andExpect(toExactlyContainKeys(
                        "vespasian",
                        "vitellius",
                        "otho"
                ));
    }

    @Test
    void getDataWithNotNullFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?wikiqid:not=null"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(4))
                .andExpect(toExactlyContainKeys(
                        "otho",
                        "titus",
                        "vespasian",
                        "vitellius"
                ));
    }

    @Test
    void getDataWithNullFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?wikiqid=null"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(19));
    }

    @Test
    void getDataWithNonExistingFilterReturnsEmptyList() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?color=blue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(0));
    }

    @Test
    void getDataThrowsBadRequestForInvalidFilterOperator() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?wikiqid:invalidOperator=null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDataThrowsBadRequestForMissingFilterValue() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?wikiqid="))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDataIncludesAppropriateMetadata() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/mesh?start:gt=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.query.filters.length()").value(1))
                .andExpect(jsonPath("$.meta.query.filters.[0].attribute").value("start"))
                .andExpect(jsonPath("$.meta.query.filters.[0].operator").value("GREATER_THAN"))
                .andExpect(jsonPath("$.meta.query.filters.[0].value").value("0032-04-28"));
    }

}

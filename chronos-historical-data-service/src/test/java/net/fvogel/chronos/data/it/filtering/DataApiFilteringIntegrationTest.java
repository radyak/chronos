package net.fvogel.chronos.data.it.filtering;

import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static net.fvogel.chronos.data.testutils.DataApiRequestMatcher.toExactlyMatchKeys;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiFilteringIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void getDataWithFilterParamReturnsMatchingEntry() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?qid=Q1416"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(toExactlyMatchKeys("otho"));
    }

    @Test
    void getDataWithNonmatchingFilterParamReturnsNoEntry() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?qid=DOESNOTEXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getDataWithGreaterThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?sortBy=from&pageSize=5&from:gt=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(toExactlyMatchKeys(
                        "titus",
                        "domitian",
                        "valerian-i",
                        "victorinus",
                        "tacitus"
                ));
    }

    @Test
    void getDataWithGreaterEqualsThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?sortBy=from&pageSize=5&from:gte=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(toExactlyMatchKeys(
                        "otho",
                        "titus",
                        "domitian",
                        "valerian-i",
                        "victorinus"
                ));
    }

    @Test
    void getDataWithLessThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?sortBy=from&pageSize=5&from:lt=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(toExactlyMatchKeys(
                        "vespasian",
                        "vitellius"
                ));
    }

    @Test
    void getDataWithLessEqualsThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?sortBy=from&pageSize=5&from:lte=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(toExactlyMatchKeys(
                        "vespasian",
                        "vitellius",
                        "otho"
                ));
    }

    @Test
    void getDataWithNotNullFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?sortBy=key&pageSize=5&qid:not=null"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(toExactlyMatchKeys(
                        "otho",
                        "titus",
                        "vespasian",
                        "vitellius"
                ));
    }

    @Test
    void getDataWithNullFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?sortBy=key&pageSize=5&qid=null"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(toExactlyMatchKeys(
                        "antiochus",
                        "aurelian",
                        "caenis",
                        "claudius-gothicus",
                        "domitian"
                ));
    }

    @Test
    void getDataWithNonExistingFilterReturnsEmptyList() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?color=blue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getDataThrowsBadRequestForInvalidFilterOperator() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?sortBy=key&pageSize=5&qid:invalidOperator=null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDataThrowsBadRequestForMissingFilterValue() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data?sortBy=key&pageSize=5&qid="))
                .andExpect(status().isBadRequest());
    }

}

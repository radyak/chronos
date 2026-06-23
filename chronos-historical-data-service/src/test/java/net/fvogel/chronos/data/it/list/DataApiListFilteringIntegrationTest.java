package net.fvogel.chronos.data.it.list;

import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static net.fvogel.chronos.data.testutils.DataApiRequestMatcher.toExactlyMatchKeys;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiListFilteringIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void getDataWithFilterParamReturnsMatchingEntry() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?wikiqid=Q1416"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(1))
                .andExpect(toExactlyMatchKeys("otho"));
    }

    @Test
    void getDataWithNonmatchingFilterParamReturnsNoEntry() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?wikiqid=DOESNOTEXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(0));
    }

    @Test
    void getDataWithGreaterThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=start&pageSize=5&start:gt=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(5))
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
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=start&pageSize=5&start:gte=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(5))
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
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=start&pageSize=5&start:lt=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(2))
                .andExpect(toExactlyMatchKeys(
                        "vespasian",
                        "vitellius"
                ));
    }

    @Test
    void getDataWithLessEqualsThanFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=start&pageSize=5&start:lte=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(3))
                .andExpect(toExactlyMatchKeys(
                        "vespasian",
                        "vitellius",
                        "otho"
                ));
    }

    @Test
    void getDataWithNotNullFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=key&pageSize=5&wikiqid:not=null"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(4))
                .andExpect(toExactlyMatchKeys(
                        "otho",
                        "titus",
                        "vespasian",
                        "vitellius"
                ));
    }

    @Test
    void getDataWithNullFilterParamReturnsMatchingEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=key&pageSize=5&wikiqid=null"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(5))
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
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?color=blue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(0));
    }

    @Test
    void getDataThrowsBadRequestForInvalidFilterOperator() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=key&pageSize=5&wikiqid:invalidOperator=null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDataThrowsBadRequestForMissingFilterValue() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=key&pageSize=5&wikiqid="))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDataIncludesAppropriateMetadata() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=key&pageSize=5&start:gt=0032-04-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.query.filters.length()").value(1))
                .andExpect(jsonPath("$.meta.query.filters.[0].attribute").value("start"))
                .andExpect(jsonPath("$.meta.query.filters.[0].operator").value("GREATER_THAN"))
                .andExpect(jsonPath("$.meta.query.filters.[0].value").value("0032-04-28"));
    }

}

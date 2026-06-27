package net.fvogel.chronos.data.it.mesh;

import net.fvogel.chronos.data.model.query.ConditionOperator;
import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static net.fvogel.chronos.data.testutils.DataApiRequestMatcher.toExactlyContainKeys;
import static net.fvogel.chronos.data.testutils.MeshQueryBuilder.query;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiMeshFilteringIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void searchMeshWithFilterParamReturnsMatchingEntry() throws Exception {
        var query = query()
                .withFilter("wikiqid", ConditionOperator.EQUAL, "Q1416")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(1))
                .andExpect(toExactlyContainKeys("otho"));
    }

    @Test
    void searchMeshWithNonmatchingFilterParamReturnsNoEntry() throws Exception {
        var query = query()
                .withFilter("wikiqid", ConditionOperator.EQUAL, "DOESNOTEXIST")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(0));
    }

    @Test
    void searchMeshWithGreaterThanFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withFilter("start", ConditionOperator.GREATER_THAN, "0032-04-28")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(13));
    }

    @Test
    void searchMeshWithGreaterEqualsThanFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withFilter("start", ConditionOperator.GREATER_EQUAL_THAN, "0032-04-28")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(14));
    }

    @Test
    void searchMeshWithLessThanFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withFilter("start", ConditionOperator.LESS_THAN, "0032-04-28")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(2))
                .andExpect(toExactlyContainKeys(
                        "vespasian",
                        "vitellius"
                ));
    }

    @Test
    void searchMeshWithLessEqualsThanFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withFilter("start", ConditionOperator.LESS_EQUAL_THAN, "0032-04-28")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(3))
                .andExpect(toExactlyContainKeys(
                        "vespasian",
                        "vitellius",
                        "otho"
                ));
    }

    @Test
    void searchMeshWithNotNullFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withFilter("wikiqid", ConditionOperator.NOT, null)
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
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
    void searchMeshWithNullFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withFilter("wikiqid", ConditionOperator.EQUAL, null)
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(19));
    }

    @Test
    void searchMeshWithNonExistingFilterReturnsEmptyList() throws Exception {
        var query = query()
                .withFilter("color", ConditionOperator.EQUAL, "blue")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(0));
    }

    @Test
    void searchMeshThrowsBadRequestForInvalidFilterOperator() throws Exception {
        var query = query()
                .withFilter("start", ConditionOperator.EQUAL, "0032-04-28")
                .build();
        var queryString = objectMapper.writeValueAsString(query).replace("EQUAL", "invalidOperator");
        System.out.println("Sending: " + queryString);
        mvc.perform(post("/api/data/mesh")
                        .content(queryString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchMeshIncludesAppropriateMetadata() throws Exception {
        var query = query()
                .withFilter("start", ConditionOperator.GREATER_THAN, "0032-04-28")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.query.entryFilters.length()").value(1))
                .andExpect(jsonPath("$.meta.query.entryFilters.[0].attribute").value("start"))
                .andExpect(jsonPath("$.meta.query.entryFilters.[0].operator").value("GREATER_THAN"))
                .andExpect(jsonPath("$.meta.query.entryFilters.[0].value").value("0032-04-28"));
    }


    @Test
    void searchMeshWithSingleLabelsFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withFilter("Territory")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(3))
                .andExpect(toExactlyContainKeys("roman-empire", "palmyrene-empire", "gallic-empire"));
    }

    @Test
    void searchMeshWithMultipleLabelsFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withFilter("Territory", "Person")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(23));
    }

    @Test
    void searchMeshWithLabelsFilterParamWorksWithFilterAndSortParams() throws Exception {
        var query = query()
                .withFilter("Territory")
                .withFilter("start", ConditionOperator.GREATER_EQUAL_THAN, "0200")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(2))
                .andExpect(toExactlyContainKeys("palmyrene-empire", "gallic-empire"));
    }

    @Test
    void searchMeshLabelsInSameFilterHaveHigherPriorityThanOtherFilterCriteria() throws Exception {
        var query = query()
                .withFilter("key", ConditionOperator.EQUAL, "vespasian")
                .build();
        query.getEntryFilters().get(0).setLabels(List.of("Territory"));
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(3))
                .andExpect(toExactlyContainKeys("palmyrene-empire", "gallic-empire", "roman-empire"));
    }

}

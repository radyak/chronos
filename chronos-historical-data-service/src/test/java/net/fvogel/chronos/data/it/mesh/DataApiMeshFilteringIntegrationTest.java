package net.fvogel.chronos.data.it.mesh;

import net.fvogel.chronos.data.model.query.ConditionOperator;
import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static net.fvogel.chronos.data.model.query.ConditionOperator.CONTAINS;
import static net.fvogel.chronos.data.model.query.ConditionOperator.EQUAL;
import static net.fvogel.chronos.data.model.query.ConditionOperator.GREATER_THAN;
import static net.fvogel.chronos.data.testutils.DataApiRequestMatcher.toExactlyContainKeys;
import static net.fvogel.chronos.data.testutils.EntryFilterBuilder.entryFilter;
import static net.fvogel.chronos.data.testutils.MeshQueryBuilder.query;
import static net.fvogel.chronos.data.testutils.RelationFilterBuilder.relationFilter;
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
                .withEntryFilter("wikiqid", ConditionOperator.EQUAL, "Q1416")
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
                .withEntryFilter("wikiqid", ConditionOperator.EQUAL, "DOESNOTEXIST")
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
                .withEntryFilter("start", ConditionOperator.GREATER_THAN, "0032-04-28")
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
                .withEntryFilter("start", ConditionOperator.GREATER_EQUAL_THAN, "0032-04-28")
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
                .withEntryFilter("start", ConditionOperator.LESS_THAN, "0032-04-28")
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
                .withEntryFilter("start", ConditionOperator.LESS_EQUAL_THAN, "0032-04-28")
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
                .withEntryFilter("wikiqid", ConditionOperator.NOT, null)
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
                .withEntryFilter("wikiqid", ConditionOperator.EQUAL, null)
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(19));
    }

    @Test
    void searchMeshWithContainsFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withEntryFilter("Person")
                .withRelationFilter(
                        relationFilter()
                                .withTypes("RULED")
                                .withAttribute("titles", CONTAINS, "king")
                                .build()
                )
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(4))
                .andExpect(jsonPath("$.relations.length()").value(3))
                .andExpect(toExactlyContainKeys("vaballathus", "zenobia", "palmyrene-empire", "antiochus"));
    }

    @Test
    void searchMeshWithTargetEntryFilterParamReturnsMatchingEntries() throws Exception {
        var query = query()
                .withEntryFilter("key", EQUAL, "vespasian")
                .withRelationFilter(
                        relationFilter()
                                .withTargetEntryFilter(entryFilter().withLabels("Person").build())
                                .build()
                )
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(5))
                .andExpect(jsonPath("$.relations.length()").value(4))
                .andExpect(toExactlyContainKeys("vespasian", "domitilla-the-elder", "titus", "domitian", "caenis"));
    }

    @Test
    void searchMeshWithNonExistingFilterReturnsEmptyList() throws Exception {
        var query = query()
                .withEntryFilter("color", ConditionOperator.EQUAL, "blue")
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
                .withEntryFilter("start", ConditionOperator.EQUAL, "0032-04-28")
                .build();
        var queryString = objectMapper.writeValueAsString(query).replace("EQUAL", "invalidOperator");
        mvc.perform(post("/api/data/mesh")
                        .content(queryString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchMeshThrowsBadRequestForUnspecifiedFilterOperator() throws Exception {
        var query = query()
                .withEntryFilter("start", null, "0032-04-28")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchMeshThrowsBadRequestForUnspecifiedAttribute() throws Exception {
        var query = query()
                .withEntryFilter(null, EQUAL, "0032-04-28")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchMeshThrowsBadRequestForIncompatibleValue() throws Exception {
        var query = query()
                .withEntryFilter("start", GREATER_THAN, null)
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchMeshIncludesAppropriateMetadata() throws Exception {
        var query = query()
                .withEntryFilter("start", ConditionOperator.GREATER_THAN, "0032-04-28")
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
                .withEntryFilter("Territory")
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
                .withEntryFilter("Territory", "Person")
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
                .withEntryFilter("Territory")
                .withEntryFilter("start", ConditionOperator.GREATER_EQUAL_THAN, "0200")
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
                .withEntryFilter("key", ConditionOperator.EQUAL, "vespasian")
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

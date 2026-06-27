package net.fvogel.chronos.data.it.mesh;

import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static net.fvogel.chronos.data.model.query.ConditionOperator.EQUAL;
import static net.fvogel.chronos.data.testutils.DataApiRequestMatcher.toExactlyContainKeys;
import static net.fvogel.chronos.data.testutils.MeshQueryBuilder.query;
import static net.fvogel.chronos.data.testutils.RelationFilterBuilder.relationFilter;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiMeshRelationsIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void searchMeshWithoutRelationsReturnsNoRelations() throws Exception {
        var query = query()
                .withEntryFilter("key", EQUAL, "vespasian")
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(1))
                .andExpect(jsonPath("$.relations.length()").value(0))
                .andExpect(toExactlyContainKeys("vespasian"));
    }

    @Test
    void searchMeshWithRelationsWildcardReturnsAllRelationsOfAllTypesAndRelatedEntries() throws Exception {
        var query = query()
                .withEntryFilter("key", EQUAL, "vespasian")
                .withRelationFilter(
                        relationFilter()
                                .withTypes("*")
                                .build()
                )
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(6))
                .andExpect(jsonPath("$.relations.length()").value(5))
                .andExpect(jsonPath("$.length($.relations[?(@.type == 'RULED')].length())").value(1))
                .andExpect(jsonPath("$.length($.relations[?(@.type == 'CHILD_OF')].length())").value(2))
                .andExpect(jsonPath("$.length($.relations[?(@.type == 'RELATIONSHIP_WITH')].length())").value(2))
                .andExpect(toExactlyContainKeys("vespasian", "caenis", "titus", "domitian", "domitilla-the-elder", "roman-empire"));
    }

    @Test
    void searchMeshWithSpecifiedRelationsTypesReturnsAllRelationsOfSpecifiedTypesAndRelatedEntries() throws Exception {
        var query = query()
                .withEntryFilter("key", EQUAL, "vespasian")
                .withRelationFilter(
                        relationFilter()
                                .withTypes("RULED", "RELATIONSHIP_WITH")
                                .build()
                )
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(4))
                .andExpect(jsonPath("$.relations.length()").value(3))
                .andExpect(jsonPath("$.length($.relations[?(@.type == 'RULED')].length())").value(1))
                .andExpect(jsonPath("$.length($.relations[?(@.type == 'CHILD_OF')].length())").value(0))
                .andExpect(jsonPath("$.length($.relations[?(@.type == 'RELATIONSHIP_WITH')].length())").value(2))
                .andExpect(toExactlyContainKeys("vespasian", "caenis", "domitilla-the-elder", "roman-empire"));
    }

    @Test
    void searchMeshWithUndefinedRelationsTypesReturnsNoEntry() throws Exception {
        var query = query()
                .withEntryFilter("key", EQUAL, "vespasian")
                .withRelationFilter(
                        relationFilter()
                                .withTypes("UNKNOWN")
                                .build()
                )
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(0))
                .andExpect(jsonPath("$.relations.length()").value(0))
                .andExpect(toExactlyContainKeys());
    }

    @Test
    void searchMeshWithoutAnyParamsReturnsAllEntries() throws Exception {
        var query = query().build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(23))
                .andExpect(jsonPath("$.relations.length()").value(0));
    }

    @Test
    void searchMeshWithSpecifiedRelationTypeReturnsAssociatedEntries() throws Exception {
        var query = query()
                .withRelationFilter(
                        relationFilter()
                                .withTypes("SECESSIONAL_TO")
                                .build()
                )
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(3))
                .andExpect(jsonPath("$.relations.length()").value(2))
                .andExpect(toExactlyContainKeys("roman-empire", "gallic-empire", "palmyrene-empire"));
    }

    @Test
    void searchMeshWithSpecifiedRelationTypeAndAttributeReturnsAssociatedEntries() throws Exception {
        var query = query()
                .withEntryFilter("key", EQUAL, "vespasian")
                .withRelationFilter(
                        relationFilter()
                                .withTypes("RELATIONSHIP_WITH")
                                .withAttribute("status", EQUAL, "married")
                                .build()
                )
                .build();
        mvc.perform(post("/api/data/mesh")
                        .content(objectMapper.writeValueAsString(query))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(2))
                .andExpect(jsonPath("$.relations.length()").value(1))
                .andExpect(toExactlyContainKeys("vespasian", "domitilla-the-elder"));
    }

}

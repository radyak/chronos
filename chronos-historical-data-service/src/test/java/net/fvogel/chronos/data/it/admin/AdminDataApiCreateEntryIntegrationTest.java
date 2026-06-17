package net.fvogel.chronos.data.it.admin;

import net.fvogel.chronos.data.client.SchemaClient;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Set;

import static net.fvogel.chronos.data.testutils.DefaultTestEntries.maximalPerson;
import static net.fvogel.chronos.data.testutils.DefaultTestEntries.minimalPerson;
import static net.fvogel.chronos.data.testutils.MockResponseLoader.loadMockSchemaResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("debug")
public class AdminDataApiCreateEntryIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @MockitoBean
    SchemaClient schemaClient;

    @BeforeEach
    public void setUp() {
        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("Person.json"));
    }

    @Test
    void canCreateMinimalEntry() throws Exception {
        Entry entry = minimalPerson();
        assertTrue(dataService.findByKey("test-person").isEmpty());
        mvc.perform(post("/api/data/admin")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assertTrue(dataService.findByKey("test-person").isPresent());
    }

    @Test
    void canCreateEntryWithAllTypes() throws Exception {
        Entry entry = maximalPerson();
        var key = (String) entry.getAttributes().get("key");

        assertTrue(dataService.findByKey(key).isEmpty());
        mvc.perform(post("/api/data/admin")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertTrue(dataService.findByKey(key).isPresent());
        Entry saveEntry = dataService.findByKey(key).get();
        assertThat(saveEntry.getLabels(), is(Set.of("Person")));

        assertThat(saveEntry.getAttributes().get("key"), is(key));
        assertThat(saveEntry.getAttributes().get("gender"), is("female"));
        assertThat(saveEntry.getAttributes().get("name"), is("Test Person Name"));
        assertThat(saveEntry.getAttributes().get("start"), is("1745-07-26"));
        assertThat(saveEntry.getAttributes().get("end"), is("1789-07-25"));
        assertThat(saveEntry.getAttributes().get("height"), is(178));
        assertThat(saveEntry.getAttributes().get("wikiqid"), is("Q1234"));

        assertThat(saveEntry.get_meta().getCreateAuthor(), is("admin"));
    }

    @Nested
    public class ValidationTest {

        @Test
        void throwsBadRequestOnDuplicateKey() throws Exception {
            assertTrue(dataService.findByKey("vespasian").isPresent());

            Entry entry = maximalPerson();
            entry.getAttributes().put("key", "vespasian");
            mvc.perform(post("/api/data/admin")
                            .content(objectMapper.writeValueAsString(entry))
                            .header("Authorization", adminAuthHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.length()").value(1))
                    .andExpect(jsonPath("$.errors.[0].field").value("key"))
                    .andExpect(jsonPath("$.errors.[0].constraint").value("UNIQUE"))
                    .andExpect(jsonPath("$.errors.[0].arguments.value").value("vespasian"));
        }

        @Test
        void throwsBadRequestOnSeveralValidationErrors() throws Exception {
            Entry entry = dataService.findByKey("vespasian").get();

            // ALLOWED_VALUES
            entry.getAttributes().put("gender", "UNKNOWN");
            // CORRECT_TYPE
            entry.getAttributes().put("height", "178");
            // DEFINED_ATTRIBUTES
            entry.getAttributes().put("random-attr", "random-value");

            mvc.perform(put("/api/data/admin/{key}", "vespasian")
                            .content(objectMapper.writeValueAsString(entry))
                            .header("Authorization", adminAuthHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.length()").value(3))
                    // gender: violated ALLOWED_VALUES
                    .andExpect(jsonPath("$.errors[?(@.field == 'gender')].constraint").value("ALLOWED_VALUES"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'gender')].arguments.value").value("UNKNOWN"))
                    // height: violated CORRECT_TYPE
                    .andExpect(jsonPath("$.errors[?(@.field == 'height')].constraint").value("CORRECT_TYPE"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'height')].arguments.value").value("178"))
                    // random-attr: violated DEFINED_ATTRIBUTES
                    .andExpect(jsonPath("$.errors[?(@.field == 'random-attr')].constraint").value("DEFINED_ATTRIBUTES"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'random-attr')].arguments.value").value("random-value"));
        }

    }

    @Nested
    public class SecurityTest {

        @Test
        void unauthenticatedUserCannotCreateEntry() throws Exception {
            Entry entry = minimalPerson();
            Assertions.assertThat(dataService.findByKey("test-person").isEmpty());
            mvc.perform(post("/api/data/admin")
                    .content(objectMapper.writeValueAsString(entry))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
            Assertions.assertThat(dataService.findByKey("test-person").isEmpty());
        }

        @Test
        void unauthorizedUserCannotCreateEntry() throws Exception {
            Entry entry = minimalPerson();
            Assertions.assertThat(dataService.findByKey("test-person").isEmpty());
            mvc.perform(post("/api/data/admin")
                    .content(objectMapper.writeValueAsString(entry))
                    .header("Authorization", authHeader("user"))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isForbidden());
            Assertions.assertThat(dataService.findByKey("test-person").isEmpty());
        }

    }

}

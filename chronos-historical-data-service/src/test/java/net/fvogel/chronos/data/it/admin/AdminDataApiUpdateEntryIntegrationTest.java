package net.fvogel.chronos.data.it.admin;

import net.fvogel.chronos.data.client.SchemaClient;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
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

import java.time.LocalDate;
import java.util.Set;

import static net.fvogel.chronos.data.testutils.DefaultTestEntries.minimalPerson;
import static net.fvogel.chronos.data.testutils.MockResponseLoader.loadMockSchemaResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("debug")
public class AdminDataApiUpdateEntryIntegrationTest extends BaseIntegrationTest {

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
    void canUpdateStringAttribute() throws Exception {
        Entry entry = dataService.findByKey("vespasian").get();
        assertThat(entry.getAttributes().get("name"), is("Titus Flavius Vespasianus"));
        entry.getAttributes().put("name", "Vespasian");
        mvc.perform(put("/api/data/admin/{key}", "vespasian")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Entry updatedEntry = dataService.findByKey("vespasian").get();
        assertThat(updatedEntry.getAttributes().get("name"), is("Vespasian"));
    }

    @Test
    void canUpdateAttributeToNull() throws Exception {
        Entry entry = dataService.findByKey("vespasian").get();
        assertThat(entry.getAttributes().get("name"), is("Titus Flavius Vespasianus"));
        entry.getAttributes().put("name", null);
        mvc.perform(put("/api/data/admin/{key}", "vespasian")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Entry updatedEntry = dataService.findByKey("vespasian").get();
        assertThat(updatedEntry.getAttributes().get("name"), nullValue());
    }

    @Test
    void cannotUpdateMetaInfo() throws Exception {
        Entry entry = dataService.findByKey("vespasian").get();
        entry.get_meta().setVersion(17);
        entry.get_meta().setCreateDate(LocalDate.of(1848, 5, 17).toString());
        entry.get_meta().setCreateAuthor("hacker");
        mvc.perform(put("/api/data/admin/{key}", "vespasian")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Entry updatedEntry = dataService.findByKey("vespasian").get();
        assertThat(updatedEntry.get_meta().getVersion(), is(2));
        assertThat(updatedEntry.get_meta().getCreateDate(), nullValue());
        assertThat(updatedEntry.get_meta().getCreateAuthor(), nullValue());
        assertThat(updatedEntry.get_meta().getLastUpdateDate(), notNullValue());
        assertThat(updatedEntry.get_meta().getLastUpdateAuthor(), is("admin"));
    }

    @Test
    void throwsNotFoundOnUnknownKey() throws Exception {
        Entry entry = minimalPerson();
        mvc.perform(put("/api/data/admin/{key}", "unknown-key")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());

        assertTrue(dataService.findByKey("unknown-key").isEmpty());
    }

    @Test
    void cannotUpdateLabels() throws Exception {
        Entry entry = dataService.findByKey("vespasian").get();
        assertThat(entry.getLabels(), is(Set.of("Person")));
        entry.setLabels(Set.of("Territory"));
        mvc.perform(put("/api/data/admin/{key}", "vespasian")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Entry updatedEntry = dataService.findByKey("vespasian").get();
        assertThat(updatedEntry.getLabels(), is(Set.of("Person")));
    }

    // TODO: This should be prevented with GH-23 -> ValidationTest
    @Test
    void canUpdateKey() throws Exception {
        Entry entry = dataService.findByKey("vespasian").get();
        entry.getAttributes().put("key", "vespasian-changed");
        mvc.perform(put("/api/data/admin/{key}", "vespasian")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertTrue(dataService.findByKey("vespasian-changed").isPresent());
    }

    @Nested
    public class ValidationTest {

        @Test
        void throwsBadRequestOnSeveralValidationErrors() throws Exception {
            Entry entry = dataService.findByKey("vespasian").get();

            // ALLOWED_VALUES
            entry.getAttributes().put("gender", "UNKNOWN");
            // CORRECT_TYPE: String <-> Number
            entry.getAttributes().put("name", 178);
            // CORRECT_TYPE: Date <-> String
            entry.getAttributes().put("start", "UNKNOWN");
            // NO_UNKNOWN_TYPE
            entry.getAttributes().put("random-attr", "random-value");

            mvc.perform(put("/api/data/admin/{key}", "vespasian")
                    .content(objectMapper.writeValueAsString(entry))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest());

            Entry updatedEntry = dataService.findByKey("vespasian").get();
            assertThat(updatedEntry.getAttributes().get("gender"), nullValue());
            assertThat(updatedEntry.getAttributes().get("name"), is("Titus Flavius Vespasianus"));
            assertThat(updatedEntry.getAttributes().get("start"), is("0009-09-17"));
            assertThat(updatedEntry.getAttributes().get("random-attr"), nullValue());
        }

    }

    @Nested
    public class SecurityTest {

        @Test
        void unauthenticatedUserCannotUpdateEntry() throws Exception {
            Entry entry = dataService.findByKey("vespasian").get();
            mvc.perform(put("/api/data/admin/{key}", "vespasian")
                    .content(objectMapper.writeValueAsString(entry))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void unauthorizedUserCannotUpdateEntry() throws Exception {
            Entry entry = dataService.findByKey("vespasian").get();
            mvc.perform(put("/api/data/admin/{key}", "vespasian")
                    .content(objectMapper.writeValueAsString(entry))
                    .header("Authorization", authHeader("user"))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isForbidden());
        }

    }

}

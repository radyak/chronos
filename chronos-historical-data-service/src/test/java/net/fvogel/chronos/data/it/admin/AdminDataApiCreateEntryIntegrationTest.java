package net.fvogel.chronos.data.it.admin;

import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Set;

import static net.fvogel.chronos.data.testutils.DefaultTestEntries.minimalPerson;
import static net.fvogel.chronos.data.testutils.EntryBuilder.entry;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminDataApiCreateEntryIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

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
        String[] arrayProp = {"String1", "String2", "String3"};
        Entry entry = entry("Person")
                .withProperty("key", "test-key")
                .withProperty("string-property", "StringStringString")
                .withProperty("number-property", 13)
                .withProperty("array-property", arrayProp)
                .withProperty("null-property", null)
                .build();
        assertTrue(dataService.findByKey("test-key").isEmpty());
        mvc.perform(post("/api/data/admin")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertTrue(dataService.findByKey("test-key").isPresent());
        Entry saveEntry = dataService.findByKey("test-key").get();
        assertThat(saveEntry.getLabels(), is(Set.of("Person")));
        assertThat(saveEntry.getAttributes().get("key"), is("test-key"));
        assertThat(saveEntry.getAttributes().get("string-property"), is("StringStringString"));
        assertThat(saveEntry.getAttributes().get("number-property"), is(13));
        assertThat(saveEntry.getAttributes().get("array-property"), is(List.of(arrayProp)));
        assertThat(saveEntry.getAttributes().get("null-property"), nullValue());

        assertThat(saveEntry.get_meta().getCreateAuthor(), is("admin"));
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

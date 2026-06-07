package net.fvogel.chronos.data.it.admin;

import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static net.fvogel.chronos.data.testutils.DefaultTestEntries.minimalPerson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminDataApiUpdateEntryIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

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
    void canSetNumberAttribute() throws Exception {
        Entry entry = dataService.findByKey("vespasian").get();
        entry.getAttributes().put("known-children", 3);
        mvc.perform(put("/api/data/admin/{key}", "vespasian")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Entry updatedEntry = dataService.findByKey("vespasian").get();
        assertThat(updatedEntry.getAttributes().get("known-children"), is(3));
    }

    @Test
    void canSetArrayAttribute() throws Exception {
        Entry entry = dataService.findByKey("vespasian").get();
        assertThat(entry.getAttributes().get("name"), is("Titus Flavius Vespasianus"));
        entry.getAttributes().put("name", List.of("Titus", "Flavius", "Vespasianus"));
        mvc.perform(put("/api/data/admin/{key}", "vespasian")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Entry updatedEntry = dataService.findByKey("vespasian").get();
        assertThat(updatedEntry.getAttributes().get("name"), is(List.of("Titus", "Flavius", "Vespasianus")));
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

    // TODO: This should be prevented with GH-23
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

}

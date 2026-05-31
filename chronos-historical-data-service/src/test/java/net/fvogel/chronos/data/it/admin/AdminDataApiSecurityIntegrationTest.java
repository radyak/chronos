package net.fvogel.chronos.data.it.admin;

import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static net.fvogel.chronos.data.testutils.DefaultTestEntries.minimalPerson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminDataApiSecurityIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void unauthenticatedUserCannotCreateEntry() throws Exception {
        Entry entry = minimalPerson();
        assertThat(dataService.findByKey("test-person").isEmpty());
        mvc.perform(post("/api/data/admin")
                .content(objectMapper.writeValueAsString(entry))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
        assertThat(dataService.findByKey("test-person").isEmpty());
    }

    @Test
    void unauthorizedUserCannotDeleteType() throws Exception {
        Entry entry = minimalPerson();
        assertThat(dataService.findByKey("test-person").isEmpty());
        mvc.perform(post("/api/data/admin")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", authHeader("user"))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
        assertThat(dataService.findByKey("test-person").isEmpty());
    }

    @Test
    void adminRoleAuthorizedUserCanDeleteType() throws Exception {
        Entry entry = minimalPerson();
        assertThat(dataService.findByKey("test-person").isEmpty());
        mvc.perform(post("/api/data/admin")
                .content(objectMapper.writeValueAsString(entry))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assertThat(dataService.findByKey("test-person").isPresent());
    }

}

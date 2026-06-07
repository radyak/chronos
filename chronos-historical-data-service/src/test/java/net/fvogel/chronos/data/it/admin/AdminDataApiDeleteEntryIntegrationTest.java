package net.fvogel.chronos.data.it.admin;

import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminDataApiDeleteEntryIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void canDeleteEntry() throws Exception {
        assertTrue(dataService.findByKey("vespasian").isPresent());
        mvc.perform(delete("/api/data/admin/{key}", "vespasian")
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertTrue(dataService.findByKey("vespasian").isEmpty());
    }

    @Test
    void throwsNotFoundForUnknownKey() throws Exception {
        mvc.perform(delete("/api/data/admin/{key}", "unknown")
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Nested
    public class SecurityTest {

        @Test
        void unauthenticatedUserCannotDeleteEntry() throws Exception {
            mvc.perform(delete("/api/data/admin/{key}", "vespasian")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void unauthorizedUserCannotDeleteEntry() throws Exception {
            mvc.perform(delete("/api/data/admin/{key}", "vespasian")
                    .header("Authorization", authHeader("user"))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isForbidden());
        }

    }

}

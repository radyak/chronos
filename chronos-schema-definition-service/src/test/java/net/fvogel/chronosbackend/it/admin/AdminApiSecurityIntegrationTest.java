package net.fvogel.chronosbackend.it.admin;

import net.fvogel.chronosbackend.domain.schema.persistence.repository.EntityPORepository;
import net.fvogel.chronosbackend.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminApiSecurityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    EntityPORepository entityPORepository;

    @Test
    void unauthenticatedUserCannotDeleteEntity() throws Exception {
        assertThat(entityPORepository.findByKey("Territory").isPresent());
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory"))
                .andExpect(status().isUnauthorized());
        assertThat(entityPORepository.findByKey("Territory").isPresent());
    }

    @Test
    void unauthorizedUserCannotDeleteEntity() throws Exception {
        assertThat(entityPORepository.findByKey("Territory").isPresent());
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory")
                .header("Authorization", authHeader("user"))
        ).andExpect(status().isForbidden());
        assertThat(entityPORepository.findByKey("Territory").isPresent());
    }

    @Test
    void adminRoleAuthorizedUserCanDeleteEntity() throws Exception {
        assertThat(entityPORepository.findByKey("Territory").isPresent());
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory")
                .header("Authorization", adminAuthHeader())
        ).andExpect(status().isOk());
        assertThat(entityPORepository.findByKey("Territory").isEmpty());
    }

}

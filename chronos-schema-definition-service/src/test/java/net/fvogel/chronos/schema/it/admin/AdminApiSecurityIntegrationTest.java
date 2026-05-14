package net.fvogel.chronos.schema.it.admin;

import net.fvogel.chronos.schema.domain.schema.persistence.repository.TypePORepository;
import net.fvogel.chronos.schema.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminApiSecurityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    TypePORepository typePORepository;

    @Test
    void unauthenticatedUserCannotDeleteEntity() throws Exception {
        assertThat(typePORepository.findByKey("Territory").isPresent());
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory"))
                .andExpect(status().isUnauthorized());
        assertThat(typePORepository.findByKey("Territory").isPresent());
    }

    @Test
    void unauthorizedUserCannotDeleteEntity() throws Exception {
        assertThat(typePORepository.findByKey("Territory").isPresent());
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory")
                .header("Authorization", authHeader("user"))
        ).andExpect(status().isForbidden());
        assertThat(typePORepository.findByKey("Territory").isPresent());
    }

    @Test
    void adminRoleAuthorizedUserCanDeleteEntity() throws Exception {
        assertThat(typePORepository.findByKey("Territory").isPresent());
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory")
                .header("Authorization", adminAuthHeader())
        ).andExpect(status().isOk());
        assertThat(typePORepository.findByKey("Territory").isEmpty());
    }

}

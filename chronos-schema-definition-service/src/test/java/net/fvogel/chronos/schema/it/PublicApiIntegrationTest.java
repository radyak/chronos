package net.fvogel.chronos.schema.it;

import net.fvogel.chronos.schema.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class PublicApiIntegrationTest extends BaseIntegrationTest {

    @Test
    void getCompleteSchema() throws Exception {
        mvc.perform(get("/api/schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("*"))

                .andExpect(jsonPath("$.types.elements.length()").value(3))
                .andExpect(jsonPath("$.types.defaultAttributes.length()").value(4))

                .andExpect(jsonPath("$.relations.elements.length()").value(4))
                .andExpect(jsonPath("$.relations.defaultAttributes.length()").value(2));
    }

    @Test
    void getSingleType() throws Exception {
        mvc.perform(get("/api/schema/{key}", "Territory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("Territory"))

                .andExpect(jsonPath("$.types.elements.length()").value(2))
                .andExpect(jsonPath("$.types.defaultAttributes.length()").value(4))

                .andExpect(jsonPath("$.relations.elements.length()").value(2))
                .andExpect(jsonPath("$.relations.defaultAttributes.length()").value(2));
    }

    @Test
    void cannotGetUnknownType() throws Exception {
        mvc.perform(get("/api/schema/{key}", "Dynasty"))
                .andExpect(status().isNotFound());
    }

}

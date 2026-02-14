package net.fvogel.chronos.schema.it.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.EntityPORepository;
import net.fvogel.chronos.schema.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static net.fvogel.chronos.schema.testutils.builder.TestDataBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminApiEntityCreationIntegrationTest extends BaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    EntityPORepository entityPORepository;

    @Test
    void canCreateMinimalEntity() throws Exception {
        // Before: Doesn't exist
        getEntity("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                .content(objectMapper.writeValueAsString(createMinimalEntity("Event")))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // After: Exists, but without attributes or relations
        getEntity("Event").andExpect(status().isOk())
                .andExpect(jsonPath("$.entities.elements.length()").value(1))
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')]").exists())
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')].attributes").doesNotExist())
                .andExpect(jsonPath("$.relations.elements.length()").value(0));
    }

    @Test
    void canCreateFullEntityWithoutRelations() throws Exception {
        EntityPO entity = createFullDefaultEntity();

        // Before: Doesn't exist
        getEntity("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                .content(objectMapper.writeValueAsString(entity))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // After: Exists, with attributes and relations
        getEntity("Event").andExpect(status().isOk())
                .andExpect(jsonPath("$.entities.elements.length()").value(1))
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')]").exists())
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')].attributes.length()").value(4))
                .andExpect(jsonPath("$.relations.elements.length()").value(0));
    }

    @Test
    void canCreateFullEntityWithRelations() throws Exception {
        EntityPO target = entityPORepository.findByKey("Person").get();
        EntityPO entity = createFullDefaultEntityWithTarget(target);

        // Before: Doesn't exist
        getEntity("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                .content(objectMapper.writeValueAsString(entity))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // After: Exists, with attributes and relations
        getEntity("Event").andExpect(status().isOk())
                .andExpect(jsonPath("$.entities.elements.length()").value(2))
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')]").exists())
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')].attributes.length()").value(4))
                .andExpect(jsonPath("$.relations.elements.length()").value(1));
    }

    @Test
    void cannotCreateEntityWithAlreadyExistingKey() throws Exception {
        // Before: Already exists
        getEntity("Territory").andExpect(status().isOk());

        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(createMinimalEntity("Territory")))
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Data is not valid"))
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("key"))
                .andExpect(jsonPath("$.errors[0].constraint").value("Unique"))
                .andExpect(jsonPath("$.errors[0].message").value("org.chronos.schema.error.duplicate-key"))
                .andExpect(jsonPath("$.errors[0].arguments").doesNotExist());
    }

    @Test
    void cannotCreateEntityWithoutKey() throws Exception {
        EntityPO entityPO = createMinimalEntity("Territory");
        entityPO.setKey(null);

        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(entityPO))
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Data is not valid"))
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("key"))
                .andExpect(jsonPath("$.errors[0].constraint").value("NotNull"))
                .andExpect(jsonPath("$.errors[0].message").value("org.chronos.schema.error.not-specified"))
                .andExpect(jsonPath("$.errors[0].arguments").isEmpty());
    }

    @Test
    void cannotCreateEntityWithTooShortKey() throws Exception {
        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(createMinimalEntity("Te")))
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Data is not valid"))
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("key"))
                .andExpect(jsonPath("$.errors[0].constraint").value("Size"))
                .andExpect(jsonPath("$.errors[0].message").value("org.chronos.schema.error.invalid-length"))
                .andExpect(jsonPath("$.errors[0].arguments.min").value(3))
                .andExpect(jsonPath("$.errors[0].arguments.max").value(64));
    }

    @Test
    void cannotCreateEntityWithDuplicateAttributeKey() throws Exception {
        EntityPO entity = entity().withKey("Event")
                .withAttribute(createMinimalEntityAttribute("isIncidental"))
                .withAttribute(createMinimalEntityAttribute("localNames"))
                .withAttribute(createMinimalEntityAttribute("isIncidental"))
                .build();

        // Before: Doesn't exist
        getEntity("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(entity))
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Data is not valid"))
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[0].field").value("attributes[0].key"))
                .andExpect(jsonPath("$.errors[0].constraint").value("Unique"))
                .andExpect(jsonPath("$.errors[0].message").value("org.chronos.schema.error.duplicate-key"))
                .andExpect(jsonPath("$.errors[0].arguments").doesNotExist());

        // After: Still doesn't exist
        getEntity("Event").andExpect(status().isNotFound());
    }

    @Test
    void cannotCreateEntityWithoutAttributeKey() throws Exception {
        EntityPO entity = entity().withKey("Event")
                .withAttribute(createMinimalEntityAttribute("isIncidental"))
                .withAttribute(createMinimalEntityAttribute("localNames"))
                .withAttribute(createMinimalEntityAttribute(null))
                .build();

        // Before: Doesn't exist
        getEntity("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(entity))
                        .header("Authorization", adminAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Data is not valid"))
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("attributes[2].key"))
                .andExpect(jsonPath("$.errors[0].constraint").value("NotNull"))
                .andExpect(jsonPath("$.errors[0].message").value("org.chronos.schema.error.not-specified"))
                .andExpect(jsonPath("$.errors[0].arguments").isEmpty());

        // After: Still doesn't exist
        getEntity("Event").andExpect(status().isNotFound());
    }

}

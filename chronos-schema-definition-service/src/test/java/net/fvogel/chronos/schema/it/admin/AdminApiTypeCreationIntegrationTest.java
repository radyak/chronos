package net.fvogel.chronos.schema.it.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.TypePORepository;
import net.fvogel.chronos.schema.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static net.fvogel.chronos.schema.testutils.builder.TestDataBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminApiTypeCreationIntegrationTest extends BaseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    TypePORepository typePORepository;

    @Test
    void canCreateMinimalType() throws Exception {
        // Before: Doesn't exist
        getType("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                .content(objectMapper.writeValueAsString(createMinimalType("Event")))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // After: Exists, but without attributes or relations
        getType("Event").andExpect(status().isOk())
                .andExpect(jsonPath("$.entities.elements.length()").value(1))
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')]").exists())
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')].attributes").doesNotExist())
                .andExpect(jsonPath("$.relations.elements.length()").value(0));
    }

    @Test
    void canCreateFullTypeWithoutRelations() throws Exception {
        TypePO type = createFullDefaultType();

        // Before: Doesn't exist
        getType("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                .content(objectMapper.writeValueAsString(type))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // After: Exists, with attributes and relations
        getType("Event").andExpect(status().isOk())
                .andExpect(jsonPath("$.entities.elements.length()").value(1))
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')]").exists())
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')].attributes.length()").value(4))
                .andExpect(jsonPath("$.relations.elements.length()").value(0));
    }

    @Test
    void canCreateFullTypeWithRelations() throws Exception {
        TypePO target = typePORepository.findByKey("Person").get();
        TypePO type = createFullDefaultTypeWithTarget(target);

        // Before: Doesn't exist
        getType("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                .content(objectMapper.writeValueAsString(type))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // After: Exists, with attributes and relations
        getType("Event").andExpect(status().isOk())
                .andExpect(jsonPath("$.entities.elements.length()").value(2))
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')]").exists())
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Event')].attributes.length()").value(4))
                .andExpect(jsonPath("$.relations.elements.length()").value(1));
    }

    @Test
    void cannotCreateTypeWithAlreadyExistingKey() throws Exception {
        // Before: Already exists
        getType("Territory").andExpect(status().isOk());

        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(createMinimalType("Territory")))
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
    void cannotCreateTypeWithoutKey() throws Exception {
        TypePO typePO = createMinimalType("Territory");
        typePO.setKey(null);

        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(typePO))
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
    void cannotCreateTypeWithTooShortKey() throws Exception {
        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(createMinimalType("Te")))
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
    void cannotCreateTypeWithDuplicateAttributeKey() throws Exception {
        TypePO type = type().withKey("Event")
                .withAttribute(createMinimalTypeAttribute("isIncidental"))
                .withAttribute(createMinimalTypeAttribute("localNames"))
                .withAttribute(createMinimalTypeAttribute("isIncidental"))
                .build();

        // Before: Doesn't exist
        getType("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(type))
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
        getType("Event").andExpect(status().isNotFound());
    }

    @Test
    void cannotCreateTypeWithoutAttributeKey() throws Exception {
        TypePO type = type().withKey("Event")
                .withAttribute(createMinimalTypeAttribute("isIncidental"))
                .withAttribute(createMinimalTypeAttribute("localNames"))
                .withAttribute(createMinimalTypeAttribute(null))
                .build();

        // Before: Doesn't exist
        getType("Event").andExpect(status().isNotFound());

        mvc.perform(post("/api/schema/admin/entities")
                        .content(objectMapper.writeValueAsString(type))
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
        getType("Event").andExpect(status().isNotFound());
    }

}

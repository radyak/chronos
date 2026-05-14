package net.fvogel.chronos.schema.it.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.fvogel.chronos.commons.model.schema.AttributeType;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import net.fvogel.chronos.schema.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Set;

import static net.fvogel.chronos.schema.testutils.builder.TestDataBuilder.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
public class AdminApiTypeUpdateIntegrationTest {

    @Nested
    class Type extends BaseIntegrationTest {

        @Test
        void canUpdateSimpleFieldInExistingType() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getAttributes().size(), is(1));
            assertThat(type.getRelations().size(), is(2));

            type.setExplanation("An individual human");

            mvc.perform(put("/api/schema/admin/entities/Person")
                    .content(objectMapper.writeValueAsString(type))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // After: Exists, with changed attribute and old relations
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].explanation").value("An individual human"))
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].attributes.length()").value(1))
                    .andExpect(jsonPath("$.relations.length()").value(2));
        }

        @Test
        void cannotManipulateIdInExistingType() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getAttributes().size(), is(1));
            assertThat(type.getRelations().size(), is(2));

            type.setId(15L);

            mvc.perform(put("/api/schema/admin/entities/Person")
                            .content(objectMapper.writeValueAsString(type))
                            .header("Authorization", adminAuthHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Data is not valid"))
                    .andExpect(jsonPath("$.errors.length()").value(1))
                    .andExpect(jsonPath("$.errors[0].field").value("key"))
                    .andExpect(jsonPath("$.errors[0].constraint").value("Unique"))
                    .andExpect(jsonPath("$.errors[0].message").value("org.chronos.schema.error.duplicate-key"));

            // After: Exists, with changed attribute and old relations
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')]").exists());
        }

    }


    @Nested
    class Attributes extends BaseIntegrationTest {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Test
        void canUpdateSimpleFieldInAttributeOfExistingType() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getAttributes().size(), is(1));
            assertThat(type.getRelations().size(), is(2));

            type.getAttributes().stream().filter(attr -> attr.getKey().equals("gender")).findFirst().get()
                    .setExplanation("The biological sex of the individual human");

            mvc.perform(put("/api/schema/admin/entities/Person")
                    .content(objectMapper.writeValueAsString(type))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // After: Exists, with changed attribute and old relations
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].attributes.length()").value(1))
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].attributes[?(@.key == 'gender')].explanation").value("The biological sex of the individual human"))
                    .andExpect(jsonPath("$.relations.length()").value(2));
        }

        @Test
        void canAddAttributeToExistingType() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getAttributes().size(), is(1));
            assertThat(type.getRelations().size(), is(2));

            type.getAttributes().add(
                    attribute()
                            .withKey("isFictional")
                            .withAllowedValues(Set.of("true", "false", "unknown"))
                            .withType(AttributeType.ENUM)
                            .build()
            );

            mvc.perform(put("/api/schema/admin/entities/Person")
                    .content(objectMapper.writeValueAsString(type))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // After: Exists, with new attribute and old relations
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].attributes.length()").value(2))
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].attributes[?(@.key == 'isFictional')].type").value("ENUM"))
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].attributes[?(@.key == 'isFictional')].allowedValues.length()").value(3))
                    .andExpect(jsonPath("$.relations.length()").value(2));
        }

        @Test
        void cannotAddAttributeWithAlreadyExistingToToExistingType() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getAttributes().size(), is(1));
            assertThat(type.getRelations().size(), is(2));

            type.getAttributes().add(
                    attribute()
                            .withKey("gender")
                            .withAllowedValues(Set.of("male", "female", "unknown"))
                            .withType(AttributeType.ENUM)
                            .build()
            );

            mvc.perform(put("/api/schema/admin/entities/Person")
                            .content(objectMapper.writeValueAsString(type))
                            .header("Authorization", adminAuthHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Data is not valid"))
                    .andExpect(jsonPath("$.errors.length()").value(2))
                    .andExpect(jsonPath("$.errors[0].field").value("attributes[0].key"))
                    .andExpect(jsonPath("$.errors[0].constraint").value("Unique"))
                    .andExpect(jsonPath("$.errors[0].message").value("org.chronos.schema.error.duplicate-key"))
                    .andExpect(jsonPath("$.errors[1].field").value("attributes[1].key"))
                    .andExpect(jsonPath("$.errors[1].constraint").value("Unique"))
                    .andExpect(jsonPath("$.errors[1].message").value("org.chronos.schema.error.duplicate-key"))
                    .andExpect(jsonPath("$.errors[0].arguments").doesNotExist());

            // After: Exists, with old attributes and old relations
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].attributes.length()").value(1))
                    .andExpect(jsonPath("$.relations.length()").value(2));
        }


        @Test
        void canRemoveAttributeFromExistingType() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getAttributes().size(), is(1));
            assertThat(type.getRelations().size(), is(2));

            type.getAttributes().remove(0);

            mvc.perform(put("/api/schema/admin/entities/Person")
                    .content(objectMapper.writeValueAsString(type))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // After: Exists, without attributes and old relations
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].attributes.length()").doesNotHaveJsonPath())
                    .andExpect(jsonPath("$.relations.length()").value(2));
        }
    }


    @Nested
    class Relations extends BaseIntegrationTest {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Test
        void canUpdateSimpleFieldInExistingRelation() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getRelations().size(), is(2));

            RelationPO relation =
                    type.getRelations().stream()
                            .filter(rel -> rel.getKey().equals("RELATIONSHIP_WITH")).findFirst().get();
            relation.setExplanation("The relation to another Person");

            assertThat(relation.getAttributes().size(), is(1));

            mvc.perform(put("/api/schema/admin/entities/Person")
                    .content(objectMapper.writeValueAsString(type))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // After: Exists, with changed attribute
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.relations.length()").value(2))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RELATIONSHIP_WITH')].explanation").value("The relation to another Person"))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RELATIONSHIP_WITH')].attributes.length()").value(1));
        }

        @Test
        void canRemoveRelation() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getRelations().size(), is(2));

            RelationPO relation =
                    type.getRelations().stream()
                            .filter(rel -> rel.getKey().equals("RELATIONSHIP_WITH")).findFirst().get();
            type.getRelations().remove(relation);

            mvc.perform(put("/api/schema/admin/entities/Person")
                    .content(objectMapper.writeValueAsString(type))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // After: Exists, without removed relation
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.relations.elements.length()").value(1))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RELATIONSHIP_WITH')]").doesNotExist());
        }

        @Test
        void canAddRelation() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getRelations().size(), is(2));

            RelationPO relation =
                    relation()
                            .withKey("CHILD_OF")
                            .withTarget(type)
                            .withAttribute(relationAttribute()
                                    .withKey("type")
                                    .withType(AttributeType.ENUM)
                                    .withAllowedValues(Set.of("biological", "adopted", "step"))
                                    .build()
                            )
                            .build();
            type.getRelations().add(relation);

            mvc.perform(put("/api/schema/admin/entities/Person")
                    .content(objectMapper.writeValueAsString(type))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // After: Exists, with new relation
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.relations.elements.length()").value(3))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'CHILD_OF')].attributes.length()").value(1))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'CHILD_OF')].attributes[0].key").value("type"))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'CHILD_OF')].attributes[0].allowedValues[?(@ == 'adopted')]").exists());
        }

        @Test
        void canAddRelationAttribute() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getRelations().size(), is(2));

            RelationPO relation = type.getRelations().stream().filter(rel -> rel.getKey().equals("RULED")).findFirst().get();
            relation.getAttributes().add(
                    relationAttribute()
                            .withKey("legitimacy")
                            .withType(AttributeType.STRING)
                            .build()
            );

            mvc.perform(put("/api/schema/admin/entities/Person")
                    .content(objectMapper.writeValueAsString(type))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // After: Exists, with new relation
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.relations.elements.length()").value(2))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RULED')].attributes.length()").value(2))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RULED')].attributes[?(@.key == 'legitimacy')].type").value("STRING"));
        }

        @Test
        void canRemoveRelationAttribute() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getRelations().size(), is(2));

            RelationPO relation = type.getRelations().stream().filter(rel -> rel.getKey().equals("RULED")).findFirst().get();
            relation.getAttributes().removeIf(attr -> attr.getKey().equals("status"));

            mvc.perform(put("/api/schema/admin/entities/Person")
                    .content(objectMapper.writeValueAsString(type))
                    .header("Authorization", adminAuthHeader())
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // After: Exists, with new relation
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.relations.elements.length()").value(2))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RULED')].attributes.length()").value(0));
        }

        @Test
        void cannotAddRelationWithDuplicateKey() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getRelations().size(), is(2));

            RelationPO relation =
                    relation()
                            .withKey("RELATIONSHIP_WITH")
                            .withTarget(type)
                            .withAttribute(relationAttribute()
                                    .withKey("type")
                                    .withType(AttributeType.ENUM)
                                    .withAllowedValues(Set.of("married", "affair"))
                                    .build()
                            )
                            .build();
            type.getRelations().add(relation);

            mvc.perform(put("/api/schema/admin/entities/Person")
                            .content(objectMapper.writeValueAsString(type))
                            .header("Authorization", adminAuthHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Data is not valid"))
                    .andExpect(jsonPath("$.errors.length()").value(2))
                    .andExpect(jsonPath("$.errors[0].field").value("relations[0].key"))
                    .andExpect(jsonPath("$.errors[0].constraint").value("Unique"))
                    .andExpect(jsonPath("$.errors[0].message").value("org.chronos.schema.error.duplicate-key"));

            // After: Exists, with new relation
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.relations.elements.length()").value(2))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RELATIONSHIP_WITH')].attributes.length()").value(1))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RELATIONSHIP_WITH')].attributes[0].key").value("status"))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RELATIONSHIP_WITH')].attributes[0].allowedValues[?(@ == 'cohabitation')]").exists());
        }

        @Test
        void cannotAddRelationAttributeWithDuplicateKey() throws Exception {
            TypePO type = loadType("Person");
            assertThat(type.getRelations().size(), is(2));

            RelationPO relation = type.getRelations().stream().filter(rel -> rel.getKey().equals("RELATIONSHIP_WITH")).findFirst().get();
            relation.getAttributes().add(
                    relationAttribute()
                            .withKey("status")
                            .withType(AttributeType.ENUM)
                            .withAllowedValues(Set.of("married", "affair"))
                            .build()
            );

            mvc.perform(put("/api/schema/admin/entities/Person")
                            .content(objectMapper.writeValueAsString(type))
                            .header("Authorization", adminAuthHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Data is not valid"))
                    .andExpect(jsonPath("$.errors.length()").value(2))
                    .andExpect(jsonPath("$.errors[0].field").value("relations[0].attributes[0].key"))
                    .andExpect(jsonPath("$.errors[0].constraint").value("Unique"))
                    .andExpect(jsonPath("$.errors[0].message").value("org.chronos.schema.error.duplicate-key"))
                    .andExpect(jsonPath("$.errors[1].field").value("relations[0].attributes[1].key"))
                    .andExpect(jsonPath("$.errors[1].constraint").value("Unique"))
                    .andExpect(jsonPath("$.errors[1].message").value("org.chronos.schema.error.duplicate-key"));

            // After: Exists, with new relation
            getType("Person").andExpect(status().isOk())
                    .andExpect(jsonPath("$.relations.elements.length()").value(2))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RELATIONSHIP_WITH')].attributes.length()").value(1))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RELATIONSHIP_WITH')].attributes[0].key").value("status"))
                    .andExpect(jsonPath("$.relations.elements[?(@.key == 'RELATIONSHIP_WITH')].attributes[0].allowedValues[?(@ == 'cohabitation')]").exists());
        }
    }
}
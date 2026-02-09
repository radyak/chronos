package net.fvogel.chronosbackend.testutils.builder;

import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityAttributePO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationPO;

import java.util.List;

public class EntityPOBuilder {

    private final EntityPO entity;

    private EntityPOBuilder(EntityPO entity) {
        this.entity = entity;
    }

    public static EntityPOBuilder builder() {
        return new EntityPOBuilder(new EntityPO());
    }

    public EntityPOBuilder withKey(String key) {
        this.entity.setKey(key);
        return this;
    }

    public EntityPOBuilder withExamples(String examples) {
        this.entity.setExamples(examples);
        return this;
    }

    public EntityPOBuilder withExplanation(String explanation) {
        this.entity.setExplanation(explanation);
        return this;
    }

    public EntityPOBuilder withAttribute(EntityAttributePO attribute) {
        this.entity.getAttributes().add(attribute);
        return this;
    }

    public EntityPOBuilder withAttributes(EntityAttributePO... attributes) {
        this.entity.setAttributes(List.of(attributes));
        return this;
    }

    public EntityPOBuilder withRelation(RelationPO relation) {
        this.entity.getRelations().add(relation);
        return this;
    }

    public EntityPOBuilder withRelations(List<RelationPO> relations) {
        this.entity.setRelations(relations);
        return this;
    }

    public EntityPO build() {
        return this.entity;
    }
}

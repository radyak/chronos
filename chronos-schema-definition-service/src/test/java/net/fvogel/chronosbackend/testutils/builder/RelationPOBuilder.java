package net.fvogel.chronosbackend.testutils.builder;

import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationPO;

import java.util.List;

public class RelationPOBuilder {

    private final RelationPO entity;

    private RelationPOBuilder(RelationPO entity) {
        this.entity = entity;
    }

    public static RelationPOBuilder builder() {
        return new RelationPOBuilder(new RelationPO());
    }

    public RelationPOBuilder withKey(String key) {
        this.entity.setKey(key);
        return this;
    }

    public RelationPOBuilder withExamples(String examples) {
        this.entity.setExamples(examples);
        return this;
    }

    public RelationPOBuilder withExplanation(String explanation) {
        this.entity.setExplanation(explanation);
        return this;
    }

    public RelationPOBuilder withAttribute(RelationAttributePO attribute) {
        this.entity.getAttributes().add(attribute);
        return this;
    }

    public RelationPOBuilder withAttributes(RelationAttributePO... attributes) {
        this.entity.setAttributes(List.of(attributes));
        return this;
    }

    public RelationPOBuilder withTarget(EntityPO entity) {
        EntityPO target = new EntityPO();
        target.setId(entity.getId());
        this.entity.setTarget(target);
        return this;
    }

    public RelationPO build() {
        return this.entity;
    }
}

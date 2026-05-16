package net.fvogel.chronos.schema.testutils.builder;

import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;

import java.util.List;

public class RelationPOBuilder {

    private final RelationPO relation;

    private RelationPOBuilder(RelationPO relation) {
        this.relation = relation;
    }

    public static RelationPOBuilder builder() {
        return new RelationPOBuilder(new RelationPO());
    }

    public RelationPOBuilder withKey(String key) {
        this.relation.setKey(key);
        return this;
    }

    public RelationPOBuilder withExamples(String examples) {
        this.relation.setExamples(examples);
        return this;
    }

    public RelationPOBuilder withExplanation(String explanation) {
        this.relation.setExplanation(explanation);
        return this;
    }

    public RelationPOBuilder withAttribute(RelationAttributePO attribute) {
        this.relation.getAttributes().add(attribute);
        return this;
    }

    public RelationPOBuilder withAttributes(RelationAttributePO... attributes) {
        this.relation.setAttributes(List.of(attributes));
        return this;
    }

    public RelationPOBuilder withTarget(TypePO type) {
        TypePO target = new TypePO();
        target.setId(type.getId());
        this.relation.setTarget(target);
        return this;
    }

    public RelationPO build() {
        return this.relation;
    }
}

package net.fvogel.chronos.schema.testutils.builder;

import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypeAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;

import java.util.List;

public class TypePOBuilder {

    private final TypePO type;

    private TypePOBuilder(TypePO type) {
        this.type = type;
    }

    public static TypePOBuilder builder() {
        return new TypePOBuilder(new TypePO());
    }

    public TypePOBuilder withKey(String key) {
        this.type.setKey(key);
        return this;
    }

    public TypePOBuilder withExamples(String examples) {
        this.type.setExamples(examples);
        return this;
    }

    public TypePOBuilder withExplanation(String explanation) {
        this.type.setExplanation(explanation);
        return this;
    }

    public TypePOBuilder withAttribute(TypeAttributePO attribute) {
        this.type.getAttributes().add(attribute);
        return this;
    }

    public TypePOBuilder withAttributes(TypeAttributePO... attributes) {
        this.type.setAttributes(List.of(attributes));
        return this;
    }

    public TypePOBuilder withRelation(RelationPO relation) {
        this.type.getRelations().add(relation);
        return this;
    }

    public TypePOBuilder withRelations(List<RelationPO> relations) {
        this.type.setRelations(relations);
        return this;
    }

    public TypePO build() {
        return this.type;
    }
}

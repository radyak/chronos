package net.fvogel.chronos.schema.testutils.builder;

import net.fvogel.chronos.commons.model.schema.AttributeType;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationAttributePO;

import java.util.Set;

public class RelationAttributePOBuilder {

    private final RelationAttributePO attribute;

    private RelationAttributePOBuilder(RelationAttributePO attribute) {
        this.attribute = attribute;
    }

    public static RelationAttributePOBuilder builder() {
        return new RelationAttributePOBuilder(new RelationAttributePO());
    }

    public RelationAttributePOBuilder withKey(String key) {
        this.attribute.setKey(key);
        return this;
    }

    public RelationAttributePOBuilder withExplanation(String explanation) {
        this.attribute.setExplanation(explanation);
        return this;
    }

    public RelationAttributePOBuilder withExamples(String examples) {
        this.attribute.setExamples(examples);
        return this;
    }

    public RelationAttributePOBuilder withType(AttributeType type) {
        this.attribute.setType(type);
        return this;
    }

    public RelationAttributePOBuilder withIsArray(boolean isArray) {
        this.attribute.setIsArray(isArray);
        return this;
    }

    public RelationAttributePOBuilder withAllowedValues(Set<String> allowedValues) {
        this.attribute.setAllowedValues(allowedValues);
        return this;
    }

    public RelationAttributePOBuilder withIsMandatory(boolean isMandatory) {
        this.attribute.setIsMandatory(isMandatory);
        return this;
    }

    public RelationAttributePOBuilder withValuePattern(String valuePattern) {
        this.attribute.setValuePattern(valuePattern);
        return this;
    }

    public RelationAttributePOBuilder withValueRange(String valueRange) {
        this.attribute.setValueRange(valueRange);
        return this;
    }

    public RelationAttributePO build() {
        return this.attribute;
    }
}

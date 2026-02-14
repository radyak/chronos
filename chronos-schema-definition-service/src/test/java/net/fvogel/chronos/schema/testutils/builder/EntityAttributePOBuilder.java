package net.fvogel.chronos.schema.testutils.builder;

import net.fvogel.chronos.commons.model.schema.AttributeType;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityAttributePO;

import java.util.Set;

public class EntityAttributePOBuilder {

    private final EntityAttributePO attribute;

    private EntityAttributePOBuilder(EntityAttributePO attribute) {
        this.attribute = attribute;
    }

    public static EntityAttributePOBuilder builder() {
        return new EntityAttributePOBuilder(new EntityAttributePO());
    }

    public EntityAttributePOBuilder withKey(String key) {
        this.attribute.setKey(key);
        return this;
    }

    public EntityAttributePOBuilder withExplanation(String explanation) {
        this.attribute.setExplanation(explanation);
        return this;
    }

    public EntityAttributePOBuilder withExamples(String examples) {
        this.attribute.setExamples(examples);
        return this;
    }

    public EntityAttributePOBuilder withType(AttributeType type) {
        this.attribute.setType(type);
        return this;
    }

    public EntityAttributePOBuilder withIsArray(boolean isArray) {
        this.attribute.setIsArray(isArray);
        return this;
    }

    public EntityAttributePOBuilder withAllowedValues(Set<String> allowedValues) {
        this.attribute.setAllowedValues(allowedValues);
        return this;
    }

    public EntityAttributePOBuilder withIsMandatory(boolean isMandatory) {
        this.attribute.setIsMandatory(isMandatory);
        return this;
    }

    public EntityAttributePOBuilder withValuePattern(String valuePattern) {
        this.attribute.setValuePattern(valuePattern);
        return this;
    }

    public EntityAttributePOBuilder withValueRange(String valueRange) {
        this.attribute.setValueRange(valueRange);
        return this;
    }

    public EntityAttributePO build() {
        return this.attribute;
    }
}

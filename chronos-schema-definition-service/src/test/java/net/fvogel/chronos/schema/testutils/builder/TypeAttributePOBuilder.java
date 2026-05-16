package net.fvogel.chronos.schema.testutils.builder;

import net.fvogel.chronos.commons.model.schema.AttributeType;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypeAttributePO;

import java.util.Set;

public class TypeAttributePOBuilder {

    private final TypeAttributePO attribute;

    private TypeAttributePOBuilder(TypeAttributePO attribute) {
        this.attribute = attribute;
    }

    public static TypeAttributePOBuilder builder() {
        return new TypeAttributePOBuilder(new TypeAttributePO());
    }

    public TypeAttributePOBuilder withKey(String key) {
        this.attribute.setKey(key);
        return this;
    }

    public TypeAttributePOBuilder withExplanation(String explanation) {
        this.attribute.setExplanation(explanation);
        return this;
    }

    public TypeAttributePOBuilder withExamples(String examples) {
        this.attribute.setExamples(examples);
        return this;
    }

    public TypeAttributePOBuilder withType(AttributeType type) {
        this.attribute.setType(type);
        return this;
    }

    public TypeAttributePOBuilder withIsArray(boolean isArray) {
        this.attribute.setIsArray(isArray);
        return this;
    }

    public TypeAttributePOBuilder withAllowedValues(Set<String> allowedValues) {
        this.attribute.setAllowedValues(allowedValues);
        return this;
    }

    public TypeAttributePOBuilder withIsMandatory(boolean isMandatory) {
        this.attribute.setIsMandatory(isMandatory);
        return this;
    }

    public TypeAttributePOBuilder withValuePattern(String valuePattern) {
        this.attribute.setValuePattern(valuePattern);
        return this;
    }

    public TypeAttributePOBuilder withValueRange(String valueRange) {
        this.attribute.setValueRange(valueRange);
        return this;
    }

    public TypeAttributePO build() {
        return this.attribute;
    }
}

package net.fvogel.chronos.schema.domain.schema.business;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.schema.config.dev.TestDataImportConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class DefaultTypeAttributesRule {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Getter
    private final Set<Attribute> defaultTypeAttributes;

    @Getter
    private final Set<Attribute> defaultRelationAttributes;

    public DefaultTypeAttributesRule() throws IOException {
        defaultTypeAttributes = loadDefaultAttributes("defaults/type-attributes.json");
        defaultRelationAttributes = loadDefaultAttributes("defaults/relation-attributes.json");
    }

    public boolean isDefaultTypeAttribute(String attributeKey) {
        return isDefaultAttribute(attributeKey, defaultTypeAttributes);
    }

    public boolean isDefaultRelationAttribute(String attributeKey) {
        return isDefaultAttribute(attributeKey, defaultRelationAttributes);
    }

    private boolean isDefaultAttribute(String attributeKey, Set<Attribute> defaultAttributes) {
        return attributeKey != null
                && defaultAttributes.stream().noneMatch(defaultAttribute -> attributeKey.equals(defaultAttribute.getKey()));
    }

    private Set<Attribute> loadDefaultAttributes(String resourcePath) throws IOException {

        // READ & DESERIALIZE JSON
        InputStream is = TestDataImportConfig.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (is == null) {
            throw new IllegalStateException("Resource not found");
        }

        return mapper.readValue(is, new TypeReference<Set<Attribute>>() {
        });
    }
}

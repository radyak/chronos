package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.internal.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static net.fvogel.chronos.data.model.validation.ValidationConstraint.IS_ARRAY;

@Component
public class IsArrayValidationRule implements ValidationRule {

    public Collection<ValidationError> validate(Entry entry, Type type) {
        Collection<ValidationError> validationErrors = new HashSet<>();
        entry.getAttributes().forEach((key, value) -> {
            if (value == null) {
                // null-handling not responsibility of this rule
                return;
            }
            Optional<Attribute> maybeAttributeDefinition = type.getAttributes()
                    .stream()
                    .filter(attributeDefinition -> key.equals(attributeDefinition.getKey()))
                    .findFirst();

            if (maybeAttributeDefinition.isEmpty()) {
                return;
            }
            Attribute attributeDefinition = maybeAttributeDefinition.get();
            if (hasArrayTypeInconsistency(attributeDefinition, value)) {
                validationErrors.add(new ValidationError(
                        key,
                        IS_ARRAY,
                        value));
            }

        });
        return validationErrors;
    }

    private boolean hasArrayTypeInconsistency(Attribute attribute, Object value) {
        return attribute.getIsArray() && !(value instanceof Collection<?>);
    }

}

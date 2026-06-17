package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static net.fvogel.chronos.data.model.validation.ValidationConstraint.DEFINED_ATTRIBUTES;

@Component
public class IsDefinedAttributeValidationRule implements ValidationRule {

    public Collection<ValidationError> validate(Entry entry, Type type) {
        Collection<ValidationError> validationErrors = new HashSet<>();
        entry.getAttributes().forEach((key, value) -> {
            Optional<Attribute> maybeAttributeDefinition = type.getAttributes()
                    .stream()
                    .filter(attributeDefinition -> key.equals(attributeDefinition.getKey()))
                    .findFirst();
            if (maybeAttributeDefinition.isEmpty()) {
                validationErrors.add(new ValidationError(
                        key,
                        DEFINED_ATTRIBUTES,
                        value));
            }

        });
        return validationErrors;
    }

}

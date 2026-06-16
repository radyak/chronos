package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static net.fvogel.chronos.data.model.validation.ValidationConstraint.ALLOWED_VALUES;

@Component
public class IsAllowedValuesValidationRule implements ValidationRule {

    public Collection<ValidationError> validate(Entry entry, Type type) {
        Collection<ValidationError> validationErrors = new HashSet<>();
        entry.getAttributes().forEach((key, value) -> {
            if (value == null) {
                // null-handling not responsibility of this rule
                return;
            }
            Optional<Attribute> maybeAttributeDefinitionWithAllowedValues = type.getAttributes()
                    .stream()
                    .filter(attributeDefinition ->
                            key.equals(attributeDefinition.getKey()) && ObjectUtils.isNotEmpty(attributeDefinition.getAllowedValues())
                    )
                    .findFirst();
            if (maybeAttributeDefinitionWithAllowedValues.isEmpty()) {
                return;
            }
            Set<String> allowedValues = maybeAttributeDefinitionWithAllowedValues.get().getAllowedValues();

            if (value instanceof Collection<?>) {
                for (Object singleValue : (Collection<?>) value) {
                    if (!allowedValues.contains(singleValue)) {
                        validationErrors.add(new ValidationError(
                                key,
                                ALLOWED_VALUES,
                                singleValue));
                    }
                }
                return;
            }

            if (!allowedValues.contains(value)) {
                validationErrors.add(new ValidationError(
                        key,
                        ALLOWED_VALUES,
                        value));
            }
        });
        return validationErrors;
    }

}

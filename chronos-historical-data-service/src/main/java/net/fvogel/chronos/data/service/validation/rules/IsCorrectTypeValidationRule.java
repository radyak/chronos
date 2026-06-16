package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.fvogel.chronos.commons.model.schema.AttributeType.*;
import static net.fvogel.chronos.data.model.validation.ValidationConstraint.CORRECT_TYPE;

@Component
public class IsCorrectTypeValidationRule implements ValidationRule {

    /**
     * Explanation:
     * ^-? — start, optional minus sign (allows negative years)
     * \d{1,4} — 1 to 4 digits for the year (so 0 .. 9999, or -0 .. -9999 are allowed)
     * (?:-(0[1-9]|1[0-2]) ... )? — optional month part:
     * - Month must be 01–12
     * - If month is present, the optional day part -(0[1-9]|[12]\d|3[01]) may follow (i.e. day 01–31)
     * $ — end of string
     */
    private static final Pattern DATENOTATION_PATTERN = Pattern.compile("^-?\\d{1,4}(?:-(0[1-9]|1[0-2])(?:-(0[1-9]|[12]\\d|3[01]))?)?$");


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
            if (
                    hasStringTypeInconsistency(attributeDefinition, value)
                            || hasNumberTypeInconsistency(attributeDefinition, value)
                            || hasWikiqidTypeInconsistency(attributeDefinition, value)
                            || hasDatenotationTypeInconsistency(attributeDefinition, value)
            ) {
                validationErrors.add(new ValidationError(
                        key,
                        CORRECT_TYPE,
                        value));
            }

        });
        return validationErrors;
    }

    private boolean hasStringTypeInconsistency(Attribute attribute, Object value) {
        return STRING.equals(attribute.getType()) && !(value instanceof String);
    }

    private boolean hasNumberTypeInconsistency(Attribute attribute, Object value) {
        return NUMBER.equals(attribute.getType()) && !(value instanceof Number);
    }

    private boolean hasWikiqidTypeInconsistency(Attribute attribute, Object value) {
        return WIKIQID.equals(attribute.getType()) && !(value instanceof String && ((String) value).startsWith("Q"));
    }

    private boolean hasDatenotationTypeInconsistency(Attribute attribute, Object value) {
        if (!DATENOTATION.equals(attribute.getType())) {
            return false;
        }
        if (!(value instanceof String)) {
            return true;
        }

        Matcher m = DATENOTATION_PATTERN.matcher((String) value);
        return !m.matches();
    }

}

package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

@Component
public class IsWithinTypeRangeValidationRule implements ValidationRule {

    private static final Logger logger = LoggerFactory.getLogger(IsWithinTypeRangeValidationRule.class);

    public Collection<ValidationError> validate(Entry entry, Type type) {
        Collection<ValidationError> validationErrors = new HashSet<>();
        type.getAttributes().stream().filter(attr -> attr.getValueRange() != null).forEach(attrWithValueRange -> {
            Object value = entry.getAttributes().get(attrWithValueRange.getKey());
            if (null == value) {
                return;
            }
            String[] boundaries = attrWithValueRange.getValueRange().split("-");
            if (boundaries.length != 2) {
                logger.warn("Cannot derive value range from expression '{}' for {}.{}",
                        attrWithValueRange.getValueRange(),
                        type.getKey(),
                        attrWithValueRange.getType()
                );
            }

//            Number min = toNumber(boundaries[0]);
//            Number max = toNumber(boundaries[1]);
//
//            if ()
//            if (!cypherService.isAttributeUnique(entry, attrWithValueRange.getKey())) {
//                logger.info("Attribute {}.{}={} is not unique; adding validation error", type.getKey(), attrWithValueRange.getKey(), value);
//                validationErrors.add(new ValidationError(
//                        "attributes[" + attrWithValueRange.getKey() + "]",
//                        UNIQUE,
//                        value));
//            }
        });
        return validationErrors;
    }

    private Number toNumber(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            // Can be ignored
        }

        try {
            double d = Double.parseDouble(value);
            if (Double.isFinite(d)) {
                return d;
            }
        } catch (NumberFormatException ignored) {
            return null;
        }

        return null;

    }

}

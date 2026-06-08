package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

import static net.fvogel.chronos.data.model.validation.ValidationConstraint.MANDATORY;

@Component
public class IsMandatoryValidationRule implements ValidationRule {

    public Collection<ValidationError> validate(Entry entry, Type type) {
        Collection<ValidationError> validationErrors = new HashSet<>();
        type.getAttributes().stream().filter(Attribute::getIsMandatory).forEach(mandatoryAttr -> {
            Object value = entry.getAttributes().get(mandatoryAttr.getKey());
            if ("".equals(value) || null == value) {
                validationErrors.add(new ValidationError(
                        "attributes[" + mandatoryAttr.getKey() + "]",
                        MANDATORY,
                        value));
            }
        });
        return validationErrors;
    }

}

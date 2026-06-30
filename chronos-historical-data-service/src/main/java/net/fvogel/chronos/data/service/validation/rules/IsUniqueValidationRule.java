package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.internal.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import net.fvogel.chronos.data.service.CypherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

import static net.fvogel.chronos.data.model.validation.ValidationConstraint.UNIQUE;

@Component
public class IsUniqueValidationRule implements ValidationRule {

    private static final Logger logger = LoggerFactory.getLogger(IsUniqueValidationRule.class);

    @Autowired
    private CypherService cypherService;

    public Collection<ValidationError> validate(Entry entry, Type type) {
        Collection<ValidationError> validationErrors = new HashSet<>();
        type.getAttributes().stream().filter(Attribute::getIsUnique).forEach(uniqueAttr -> {
            Object value = entry.getAttributes().get(uniqueAttr.getKey());
            logger.info("Checking attribute {}.{} (value: {}) for uniqueness", type.getKey(), uniqueAttr.getKey(), value);
            if (null == value) {
                return;
            }
            if (!cypherService.isAttributeUnique(entry, uniqueAttr.getKey())) {
                logger.info("Attribute {}.{}={} is not unique; adding validation error", type.getKey(), uniqueAttr.getKey(), value);
                validationErrors.add(new ValidationError(
                        uniqueAttr.getKey(),
                        UNIQUE,
                        value));
            }
        });
        return validationErrors;
    }

}

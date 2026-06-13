package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.exception.InvalidDataException;
import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import net.fvogel.chronos.data.service.CypherService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static net.fvogel.chronos.data.model.validation.ValidationConstraint.UNMODIFIABLE;

// TODO: Implement
//@Component
public class IsChangeableValidationRule implements ValidationRule {

    @Autowired
    CypherService cypherService;

    public Collection<ValidationError> validate(Entry entry, Type type) {
        Collection<ValidationError> validationErrors = new HashSet<>();
        if (entry.getElementId() == null) {
            // New element, nothing to check
            return validationErrors;
        }
        var key = entry.getAttributes().get("key");
        if (key == null) {
            throw new InvalidDataException("No key specified");
        }
        var originalEntry = cypherService.findByKey((String) key).orElseThrow(NotFoundException::new);

        type.getAttributes().stream().filter(attr -> !attr.getIsChangeable()).forEach(unmodifiableAttr -> {
            Object newValue = entry.getAttributes().get(unmodifiableAttr.getKey());
            Object oldValue = originalEntry.getAttributes().get(unmodifiableAttr.getKey());
            if (!Objects.equals(newValue, oldValue)) {
                validationErrors.add(new ValidationError(
                        "attributes[" + unmodifiableAttr.getKey() + "]",
                        UNMODIFIABLE,
                        newValue));
            }
        });
        return validationErrors;
    }

}

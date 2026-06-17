package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;

import java.util.Collection;

public interface ValidationRule {
    Collection<ValidationError> validate(Entry entry, Type type);
}

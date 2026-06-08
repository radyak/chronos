package net.fvogel.chronos.data.service.validation;

import net.fvogel.chronos.commons.model.schema.SchemaResponse;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.client.SchemaClient;
import net.fvogel.chronos.data.exception.SchemaValidationException;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import net.fvogel.chronos.data.service.validation.rules.ValidationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.fvogel.chronos.data.model.validation.ValidationConstraint.NO_UNKNOWN_TYPE;
import static net.fvogel.chronos.data.model.validation.ValidationConstraint.TYPE_REQUIRED;

@Service
public class ValidationService {

    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);

    @Autowired
    SchemaClient schemaClient;

    @Autowired
    List<ValidationRule> validationRules;

    public void validate(Entry entry) {
        String entryType = getEntryType(entry);
        SchemaResponse schemaResponse = schemaClient.getType(entryType);
        Type typeDefinition = getTypeDefinition(schemaResponse, entryType);

        Type effectiveType = new Type();
        effectiveType.getAttributes().addAll(typeDefinition.getAttributes());
        effectiveType.getAttributes().addAll(schemaResponse.getTypes().getDefaultAttributes());
        logger.info("Successfully fetched schema for {}; validating: {} entry attributes <-> {} schema attributes",
                entryType,
                entry.getAttributes().size(),
                effectiveType.getAttributes().size());

        Set<ValidationError> errors = new HashSet<>();
        validationRules.forEach(validationRule -> errors.addAll(validationRule.validate(entry, effectiveType)));

        // TYPE
        // IS UNIQUE
        // IS CHANGEABLE
        // IS ARRAY
        // VALUE PATTERN
        // VALUE RANGE

        // TODO: Check Array values

        if (!errors.isEmpty()) {
            throw new SchemaValidationException(errors);
        }
    }

    private String getEntryType(Entry entry) {
        return entry.getLabels().stream().findFirst().orElseThrow(() -> {
            // Ad hoc error: no type
            var error = new ValidationError("type", TYPE_REQUIRED, entry.getLabels());
            return new SchemaValidationException(Set.of(error));
        });
    }

    private Type getTypeDefinition(SchemaResponse schemaResponse, String entryType) {
        return schemaResponse.getTypes().getElements()
                .stream()
                .filter(t -> entryType.equals(t.getKey()))
                .findFirst()
                .orElseThrow(() -> {
                    // Ad hoc error: type unknown (should not happen, since it was explicitly requested from schema service)
                    var error = new ValidationError("type", NO_UNKNOWN_TYPE, entryType);
                    return new SchemaValidationException(Set.of(error));
                });
    }

}

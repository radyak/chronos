package net.fvogel.chronos.data.exception;


import lombok.NoArgsConstructor;
import net.fvogel.chronos.data.model.validation.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@NoArgsConstructor
public class SchemaValidationException extends RuntimeException {

    private Collection<ValidationError> validationErrors;

    public SchemaValidationException(Collection<ValidationError> errors) {
        super("Validation errors");
        this.validationErrors = errors;
    }

    public Collection<ValidationError> getValidationErrors() {
        return validationErrors;
    }
}
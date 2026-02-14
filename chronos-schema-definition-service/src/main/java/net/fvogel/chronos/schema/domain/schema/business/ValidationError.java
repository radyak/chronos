package net.fvogel.chronos.schema.domain.schema.business;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ValidationError {
    private String field;
    private String constraint;
    private String message;
    private Map<String, Object> arguments;

    public ValidationError(String field, String constraint, String message) {
        this(field, constraint, message, null);
    }
}

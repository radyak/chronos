package net.fvogel.chronos.data.model.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidationError {
    String path;
    ValidationConstraint constraint;
    Object value;
}

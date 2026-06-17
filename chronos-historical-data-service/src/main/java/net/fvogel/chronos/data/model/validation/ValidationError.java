package net.fvogel.chronos.data.model.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationError {
    String path;
    ValidationConstraint constraint;
    Object value;
}

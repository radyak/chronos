package net.fvogel.chronos.data.model.validation;

public enum ValidationConstraint {
    // Type:
    TYPE_REQUIRED, NO_UNKNOWN_TYPE, CORRECT_TYPE,

    // Attribute:
    PATTERN, MANDATORY, RANGE, ALLOWED_VALUES, DEFINED_ATTRIBUTES, UNIQUE
}

package net.fvogel.chronos.data.model.validation;

public enum ValidationConstraint {
    // Type:
    TYPE_REQUIRED("org.chronos.data.error.type-required"),
    NO_UNKNOWN_TYPE("org.chronos.data.error.known-types-only"),

    // Attribute:
    CORRECT_TYPE("org.chronos.data.error.correct-type"),
    IS_ARRAY("org.chronos.data.error.is-array"),
    PATTERN("org.chronos.data.error.pattern"),
    MANDATORY("org.chronos.data.error.mandatory"),
    RANGE("org.chronos.data.error.range"),
    ALLOWED_VALUES("org.chronos.data.error.allowed-values"),
    DEFINED_ATTRIBUTES("org.chronos.data.error.known-attributes-only"),
    UNIQUE("org.chronos.data.error.unique"),
    UNMODIFIABLE("org.chronos.data.error.unmodifiable");

    private final String i18n;

    ValidationConstraint(String i18n) {
        this.i18n = i18n;
    }

    public String i18n() {
        return i18n;
    }
}

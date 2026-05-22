package net.fvogel.chronos.data.service;

import java.util.Locale;
import java.util.Objects;

public enum ConditionOperator {
    EQUAL(null),
    NOT("not"),
    GREATER_THAN("gt"),
    GREATER_EQUAL_THAN("gte"),
    LESS_THAN("lt"),
    LESS_EQUAL_THAN("lte");

    private final String value;

    ConditionOperator(String value) {
        this.value = value;
    }

    public static ConditionOperator fromValue(String value) {
        if (value == null) {
            return EQUAL;
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        for (ConditionOperator c : values()) {
            if (Objects.equals(c.value, value)) {
                return c;
            }
        }
        return null;
    }

}

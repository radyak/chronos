package net.fvogel.chronos.data.testutils;

import net.fvogel.chronos.data.model.query.ConditionOperator;
import net.fvogel.chronos.data.model.query.EntryFilter;

import java.util.List;

public class EntryFilterBuilder {

    private final EntryFilter filter = new EntryFilter();

    public static EntryFilterBuilder entryFilter() {
        return new EntryFilterBuilder();
    }

    public EntryFilterBuilder withLabels(String... relationTypes) {
        this.filter.setLabels(List.of(relationTypes));
        return this;
    }

    public EntryFilterBuilder withAttribute(String attribute, ConditionOperator operator, String value) {
        this.filter.setAttribute(attribute);
        this.filter.setOperator(operator);
        this.filter.setValue(value);
        return this;
    }

    public EntryFilter build() {
        return this.filter;
    }

}

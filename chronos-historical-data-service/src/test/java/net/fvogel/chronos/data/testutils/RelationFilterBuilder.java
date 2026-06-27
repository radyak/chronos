package net.fvogel.chronos.data.testutils;

import net.fvogel.chronos.data.model.query.ConditionOperator;
import net.fvogel.chronos.data.model.query.EntryFilter;
import net.fvogel.chronos.data.model.query.mesh.RelationFilter;

import java.util.List;

public class RelationFilterBuilder {

    private final RelationFilter filter = new RelationFilter();

    public static RelationFilterBuilder relationFilter() {
        return new RelationFilterBuilder();
    }

    public RelationFilterBuilder withTypes(String... relationTypes) {
        this.filter.setTypes(List.of(relationTypes));
        return this;
    }

    public RelationFilterBuilder withAttribute(String attribute, ConditionOperator operator, String value) {
        this.filter.setAttribute(attribute);
        this.filter.setOperator(operator);
        this.filter.setValue(value);
        return this;
    }

    public RelationFilterBuilder withTargetLabels(String... labels) {
        EntryFilter entryFilter = new EntryFilter();
        entryFilter.setLabels(List.of(labels));
        this.filter.getTargetEntryFilters().add(entryFilter);
        return this;
    }

    public RelationFilter build() {
        return this.filter;
    }

}

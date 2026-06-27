package net.fvogel.chronos.data.testutils;

import net.fvogel.chronos.data.model.query.ConditionOperator;
import net.fvogel.chronos.data.model.query.EntryFilter;
import net.fvogel.chronos.data.model.query.mesh.MeshQuery;

import java.util.List;
import java.util.Set;

public class MeshQueryBuilder {

    private final MeshQuery query = new MeshQuery();

    public static MeshQueryBuilder query() {
        return new MeshQueryBuilder();
    }

    public MeshQueryBuilder withRelations(String... relationTypes) {
        this.query.setRelationFilters(Set.of(relationTypes));
        return this;
    }

    public MeshQueryBuilder withFilter(String... labels) {
        EntryFilter entryFilter = new EntryFilter();
        entryFilter.setLabels(List.of(labels));
        this.query.getEntryFilters().add(entryFilter);
        return this;
    }

    public MeshQueryBuilder withFilter(String attribute, ConditionOperator operator, String value) {
        EntryFilter entryFilter = new EntryFilter();
        entryFilter.setAttribute(attribute);
        entryFilter.setOperator(operator);
        entryFilter.setValue(value);
        this.query.getEntryFilters().add(entryFilter);
        return this;
    }

    public MeshQuery build() {
        return this.query;
    }

}

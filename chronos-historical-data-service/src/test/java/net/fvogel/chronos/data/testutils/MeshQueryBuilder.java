package net.fvogel.chronos.data.testutils;

import net.fvogel.chronos.data.model.query.ConditionOperator;
import net.fvogel.chronos.data.model.query.EntryFilter;
import net.fvogel.chronos.data.model.query.mesh.MeshQuery;
import net.fvogel.chronos.data.model.query.mesh.RelationFilter;

import java.util.List;

public class MeshQueryBuilder {

    private final MeshQuery query = new MeshQuery();

    public static MeshQueryBuilder query() {
        return new MeshQueryBuilder();
    }

    public MeshQueryBuilder withEntryFilter(String... labels) {
        EntryFilter entryFilter = new EntryFilter();
        entryFilter.setLabels(List.of(labels));
        this.query.getEntryFilters().add(entryFilter);
        return this;
    }

    public MeshQueryBuilder withEntryFilter(String attribute, ConditionOperator operator, String value) {
        EntryFilter entryFilter = new EntryFilter();
        entryFilter.setAttribute(attribute);
        entryFilter.setOperator(operator);
        entryFilter.setValue(value);
        this.query.getEntryFilters().add(entryFilter);
        return this;
    }

    public MeshQueryBuilder withRelationFilter(RelationFilter relationFilter) {
        this.query.getRelationFilters().add(relationFilter);
        return this;
    }

    public MeshQuery build() {
        return this.query;
    }

}

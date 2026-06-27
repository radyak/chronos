package net.fvogel.chronos.data.testutils;

import net.fvogel.chronos.data.model.query.ConditionOperator;
import net.fvogel.chronos.data.model.query.Filter;
import net.fvogel.chronos.data.model.query.mesh.MeshQuery;

import java.util.List;
import java.util.Set;

public class MeshQueryBuilder {

    private final MeshQuery query = new MeshQuery();

    public static MeshQueryBuilder query() {
        return new MeshQueryBuilder();
    }

    public MeshQueryBuilder withRelations(String... relationTypes) {
        this.query.setRelations(Set.of(relationTypes));
        return this;
    }

    public MeshQueryBuilder withFilter(String... labels) {
        Filter filter = new Filter();
        filter.setLabels(List.of(labels));
        this.query.getFilters().add(filter);
        return this;
    }

    public MeshQueryBuilder withFilter(String attribute, ConditionOperator operator, String value) {
        Filter filter = new Filter();
        filter.setAttribute(attribute);
        filter.setOperator(operator);
        filter.setValue(value);
        this.query.getFilters().add(filter);
        return this;
    }

    public MeshQuery build() {
        return this.query;
    }

}

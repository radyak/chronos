package net.fvogel.chronos.data.service;

import net.fvogel.chronos.commons.exception.InvalidParameterException;
import net.fvogel.chronos.data.model.query.BaseAttributeFilter;
import net.fvogel.chronos.data.model.query.BaseQuery;
import net.fvogel.chronos.data.model.query.EntryFilter;
import net.fvogel.chronos.data.model.query.list.ListQuery;
import net.fvogel.chronos.data.model.query.list.SortOrder;
import net.fvogel.chronos.data.model.query.list.Sorting;
import net.fvogel.chronos.data.model.query.mesh.MeshQuery;
import net.fvogel.chronos.data.model.query.mesh.RelationFilter;
import org.apache.commons.lang3.ObjectUtils;
import org.neo4j.cypherdsl.core.Condition;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Property;
import org.neo4j.cypherdsl.core.PropertyContainer;
import org.neo4j.cypherdsl.core.Relationship;
import org.neo4j.cypherdsl.core.SortItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CypherDslUtils {

    private static final Logger logger = LoggerFactory.getLogger(CypherDslUtils.class);

    public static List<Condition> extractEntryConditions(BaseQuery query, Node node) {
        return query.getEntryFilters() == null ?
                Collections.emptyList() :
                query.getEntryFilters().stream()
                        .map(entryFilter -> CypherDslUtils.mapEntryFilterToCondition(entryFilter, node))
                        .toList();
    }

    public static List<Condition> extractRelationConditions(MeshQuery query, Relationship relationship) {
        return query.getRelationFilters() == null ?
                Collections.emptyList() :
                query.getRelationFilters().stream()
                        .map(relationFilter -> CypherDslUtils.mapRelationFilterToCondition(relationFilter, relationship))
                        .toList();
    }

    private static Condition mapEntryFilterToCondition(EntryFilter entryFilter, Node node) {
        Property property = node.property(Cypher.literalOf(entryFilter.getAttribute()));

        // Label filter
        if (ObjectUtils.isNotEmpty(entryFilter.getLabels())) {
            Condition condition = Cypher.noCondition();
            for (String label : entryFilter.getLabels()) {
                condition = condition.or(node.hasLabels(label));
            }
            return condition;
        }

        return getCondition(entryFilter, node);
    }

    private static Condition mapRelationFilterToCondition(RelationFilter relationFilter, Relationship relationship) {
        return getCondition(relationFilter, relationship);
    }

    private static Condition getCondition(BaseAttributeFilter filter, PropertyContainer propertyContainer) {
        Property property = propertyContainer.property(Cypher.literalOf(filter.getAttribute()));
        var value = filter.getValue();
        var operator = filter.getOperator();

        if (operator == null) {
            return Cypher.noCondition();
        }

        // specific null value handling
        if (null == filter.getValue()) {
            switch (operator) {
                case NOT -> {
                    return property.isNotNull();
                }
                case EQUAL -> {
                    return property.isNull();
                }
                default -> {
                    logger.warn("Invalid filter: {}", filter);
                    throw new InvalidParameterException();
                }
            }
        }

        switch (operator) {
            case NOT -> {
                return property.isNotEqualTo(Cypher.literalOf(value));
            }
            case GREATER_THAN -> {
                return property.gt(Cypher.literalOf(value));
            }
            case GREATER_EQUAL_THAN -> {
                return property.gte(Cypher.literalOf(value));
            }
            case LESS_THAN -> {
                return property.lt(Cypher.literalOf(value));
            }
            case LESS_EQUAL_THAN -> {
                return property.lte(Cypher.literalOf(value));
            }
            default -> {
                // EQUAL
                return property.eq(Cypher.literalOf(value));
            }
        }
    }

    // Aspect: Query domain -> CypherDSL
    public static List<SortItem> extractSortItems(ListQuery query, Node n) {
        var sortList = new ArrayList<SortItem>();
        Sorting sorting = query.getSorting().isEmpty() ? null : query.getSorting().get(0);
        if (sorting != null && sorting.getSortBy() != null) {
            var direction = sorting.getSortOrder() == SortOrder.ASC ? SortItem.Direction.ASC : SortItem.Direction.DESC;
            var property = sorting.getSortBy();
            var sort = "random".equals(property) ? Cypher.sort(Cypher.rand()) : Cypher.sort(n.property(Cypher.literalOf(property)), direction);
            sortList.add(sort);
        }
        return sortList;
    }

    public static Condition all(Condition... conditions) {
        if (conditions == null || conditions.length == 0) {
            return Cypher.noCondition();
        }
        Condition condition = conditions[0];
        for (int i = 1; i < conditions.length; i++) {
            condition = condition.and(conditions[i]);
        }
        return condition;
    }

    public static Condition all(Collection<Condition> conditions) {
        Condition[] conditionsArray = conditions.toArray(new Condition[conditions.size()]);
        return all(conditionsArray);
    }
}

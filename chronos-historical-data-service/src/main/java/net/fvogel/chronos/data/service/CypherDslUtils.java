package net.fvogel.chronos.data.service;

import net.fvogel.chronos.commons.exception.InvalidParameterException;
import net.fvogel.chronos.data.model.Filter;
import net.fvogel.chronos.data.model.ListQuery;
import net.fvogel.chronos.data.model.SortOrder;
import net.fvogel.chronos.data.model.Sorting;
import org.neo4j.cypherdsl.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CypherDslUtils {

    private static final Logger logger = LoggerFactory.getLogger(CypherDslUtils.class);

    public static List<Condition> extractConditions(ListQuery query, Node node) {
        return query.getFilters() == null ?
                Collections.emptyList() :
                query.getFilters().stream()
                        .map(filter -> CypherDslUtils.mapFilterToCondition(filter, node))
                        .toList();
    }

    private static Condition mapFilterToCondition(Filter filter, Node node) {
        Property property = node.property(Cypher.literalOf(filter.getAttribute()));
        // specific null value handling
        if (null == filter.getValue()) {
            switch (filter.getOperator()) {
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

        var value = filter.getValue();
        switch (filter.getOperator()) {
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

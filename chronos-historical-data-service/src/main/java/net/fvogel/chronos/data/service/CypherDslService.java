package net.fvogel.chronos.data.service;

import net.fvogel.chronos.commons.exception.InvalidParameterException;
import net.fvogel.chronos.data.model.Filter;
import org.neo4j.cypherdsl.core.Condition;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class CypherDslService {

    private static final Logger logger = LoggerFactory.getLogger(CypherDslService.class);

    public static Condition condition(Filter filter, Node node) {
        Property property = node.property(filter.getAttribute());
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

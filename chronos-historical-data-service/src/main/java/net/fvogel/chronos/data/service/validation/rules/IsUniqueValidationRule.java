package net.fvogel.chronos.data.service.validation.rules;

import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationError;
import net.fvogel.chronos.data.persistence.CypherClient;
import org.neo4j.cypherdsl.core.Condition;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

import static net.fvogel.chronos.data.model.validation.ValidationConstraint.UNIQUE;

@Component
public class IsUniqueValidationRule implements ValidationRule {

    private static final Logger logger = LoggerFactory.getLogger(IsUniqueValidationRule.class);

    @Autowired
    private CypherClient client;

    public Collection<ValidationError> validate(Entry entry, Type type) {
        Collection<ValidationError> validationErrors = new HashSet<>();
        type.getAttributes().stream().filter(Attribute::getIsUnique).forEach(uniqueAttr -> {
            Object value = entry.getAttributes().get(uniqueAttr.getKey());
            logger.info("Checking attribute {}.{} (value: {}) for uniqueness", type.getKey(), uniqueAttr.getKey(), value);
            if (null == value) {
                return;
            }
            if (anotherEntryExists(entry, uniqueAttr.getKey())) {
                logger.info("Attribute {}.{}={} is not unique; adding validation error", type.getKey(), uniqueAttr.getKey(), value);
                validationErrors.add(new ValidationError(
                        "attributes[" + uniqueAttr.getKey() + "]",
                        UNIQUE,
                        value));
            }
        });
        return validationErrors;
    }

    private boolean anotherEntryExists(Entry entry, String attrKey) {

        // 1. An unbounded node pattern for the "other" nodes
        Node n = Cypher.anyNode("n");

        // 2. Self-exclusion: ignore the node we're checking
        var compareElementId = entry.getElementId() == null ? "" : entry.getElementId();
        Condition notSelf = n.elementId().isNotEqualTo(Cypher.literalOf(compareElementId));

        // 3. Value equality: some other node has the same attribute value
        Condition sameValue = n.property(attrKey)
                .isEqualTo(Cypher.literalOf(entry.getAttributes().get(attrKey)));

        // 4. Combined WHERE condition
        Condition duplicateExists = notSelf.and(sameValue);

        // 5. Full statement: MATCH (n) WHERE … RETURN exists(…) AS isDuplicate
        Statement statement = Cypher.match(n)
                .where(duplicateExists)
                .returning(
                        Cypher.count(n).gt(Cypher.literalOf(0)).as("isDuplicate")
                )
                .build();

        return client.runStatement(statement, result -> result.single().get("isDuplicate").asBoolean());
    }

}

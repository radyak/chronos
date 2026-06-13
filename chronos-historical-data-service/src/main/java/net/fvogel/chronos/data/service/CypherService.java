package net.fvogel.chronos.data.service;

import net.fvogel.chronos.commons.exception.InvalidDataException;
import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.data.model.CountResult;
import net.fvogel.chronos.data.model.DataQuery;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.Pagination;
import net.fvogel.chronos.data.persistence.CypherClient;
import org.neo4j.cypherdsl.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * High-level data layer service.
 * Builds & executes cypher DSL statements.
 * Makes use of <a href="https://neo4j.github.io/cypher-dsl/2025.2.6/">Neo4j Cypher-DSL</a>
 */
@Service
public class CypherService {

    private static final Logger logger = LoggerFactory.getLogger(CypherService.class);

    @Autowired
    private CypherClient client;

    @Autowired
    private EntryMapper entryMapper;

    public List<Entry> findAll(DataQuery query) {
        var nodeName = "n";
        var n = Cypher.anyNode().named(nodeName);

        List<SortItem> sortList = CypherDslUtils.extractSortItems(query, n);
        Condition unionCondition = CypherDslUtils.all(CypherDslUtils.extractConditions(query, n));
        Pagination pagination = query.getPagination();

        var statement = Cypher.match(n)
                .where(unionCondition)
                .returning(n)
                .orderBy(sortList)
                .skip(Integer.valueOf((pagination.getPage() - 1) * pagination.getPageSize()))
                .limit(pagination.getPageSize())
                .build();

        return client.runStatement(statement, result ->
                result.list(record -> record.get(nodeName).asNode())
                        .stream().map(entryMapper::toEntry)
                        .collect(Collectors.toList())
        );
    }

    public Optional<Entry> findByKey(String key) {
        var nodeName = "n";
        var n = Cypher.anyNode().named(nodeName).withProperties("key", Cypher.literalOf(key));
        var statement = Cypher.match(n)
                .returning(n)
                .build();

        return client.runStatement(statement, result -> result
                .list(record -> record.get(nodeName).asNode())
                .stream().map(entryMapper::toEntry).findFirst()
        );
    }

    public List<CountResult> statistics() {
        var nodeName = "n";
        var n = Cypher.anyNode().named(nodeName);
        var statement = Cypher.match(n)
                .returning(Cypher.count(n).as("count"), Cypher.labels(n).as("labels"))
                .build();

        return client.runStatement(statement, result -> result.list(entryMapper::toCountResult));
    }

    public Entry create(Entry entry) {
        var n = entryMapper.toNode(entry, "n");
        var statement = Cypher.create(n).returning(n).build();

        return runAndReturn(statement);
    }

    public Entry update(String key, Entry entry) {
        var label = entry.getLabels().stream().findFirst().orElseThrow(InvalidDataException::new);
        Node node = Cypher.node(label)
                .named("n")
                .withProperties("key", Cypher.literalOf(key));

        var updateBuilder = Cypher.match(node);

        StatementBuilder.BuildableMatchAndUpdate propertyUpdateBuilder = null;
        Map<String, Object> properties = entry.getAttributes();
        entryMapper.mapMetaUpdates(properties, entry);

        for (Map.Entry<String, Object> attributeEntry : properties.entrySet()) {
            // TODO: Filter non-isChangeable attributes (GH-23)
            var value = attributeEntry.getValue() == null ?
                    Cypher.literalNull() : Cypher.literalOf(attributeEntry.getValue());
            propertyUpdateBuilder = Objects.requireNonNullElse(propertyUpdateBuilder, updateBuilder)
                    .set(node.property(attributeEntry.getKey()), value);
        }

        var statement = Objects.requireNonNullElse(propertyUpdateBuilder, updateBuilder).returning(node).build();

        return runAndReturn(statement);
    }

    public void delete(String label, String key) {
        Node node = Cypher.node(label).named("node")
                .withProperties("key", Cypher.literalOf(key));

        var statement = Cypher
                .match(node)
                .detachDelete(node)
                .build();

        client.runStatement(statement);
    }

    private Entry runAndReturn(Statement statement) {
        var resultOptional = client.runStatement(statement, result -> result
                .list(record -> record.get("n").asNode())
                .stream()
                .map(entryMapper::toEntry)
                .findFirst()
        );
        return resultOptional.orElseThrow(NotFoundException::new);
    }

    public boolean isAttributeUnique(Entry entry, String attrKey) {

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

        return client.runStatement(statement, result -> !result.single().get("isDuplicate").asBoolean());
    }

}

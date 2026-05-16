package net.fvogel.chronos.data.service;

import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.data.model.*;
import org.neo4j.cypherdsl.core.Condition;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.SortItem;
import org.neo4j.cypherdsl.core.renderer.Renderer;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CRUD Service for Neo4j data.
 * Makes use of <a href="https://neo4j.github.io/cypher-dsl/2025.2.6/">Neo4j Cypher-DSL</a>
 */
@Service
@Transactional
public class DataService {

    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private Driver driver;

    public List<Entry> findAll(DataQuery query) {
        var nodeName = "n";
        var n = Cypher.anyNode().named(nodeName);

        var sortList = new ArrayList<SortItem>();
        Sorting sorting = query.getSorting();
        if (sorting != null && sorting.getSortBy() != null) {
            var direction = sorting.getSortOrder() == SortOrder.ASC ? SortItem.Direction.ASC : SortItem.Direction.DESC;
            var property = sorting.getSortBy();
            var sort = "random".equals(property) ? Cypher.sort(Cypher.rand()) : Cypher.sort(n.property(property), direction);
            sortList.add(sort);
        }

        // TODO: Enable MultiValueMap for query.filters
        List<Condition> conditions = query.getFilters().keySet().stream()
                .map(filter -> CypherDslService.condition(filter, query.getFilters().get(filter), n))
                .toList();
        Condition union = CypherDslService.all(conditions);

        Pagination pagination = query.getPagination();
        var statement = Cypher.match(n)
                .where(union)
                .returning(n)
                .orderBy(sortList)
                .skip((pagination.getPage() - 1) * pagination.getPageSize())
                .limit(pagination.getPageSize())
                .build();
        var renderedStatement = Renderer.getDefaultRenderer().render(statement);


        try (Session session = driver.session()) {
            logger.debug("Executing statement: {}", renderedStatement);
            return session.run(renderedStatement)
                    .list(record -> record.get(nodeName).asNode())
                    .stream().map(this::mapToEntry)
                    .collect(Collectors.toList());
        }
    }


    public Entry findById(String id) {
        var nodeName = "n";
        var n = Cypher.anyNode().named(nodeName).withProperties("id", Cypher.literalOf(id));
        var statement = Cypher.match(n)
                .returning(n)
                .build();
        var renderedStatement = Renderer.getDefaultRenderer().render(statement);

        try (Session session = driver.session()) {
            return session.run(renderedStatement)
                    .list(record -> record.get(nodeName).asNode())
                    .stream().map(this::mapToEntry).findFirst()
                    .orElseThrow(NotFoundException::new);
        }
    }

    public List<CountResult> statistics() {
        var nodeName = "n";
        var n = Cypher.anyNode().named(nodeName);
        var statement = Cypher.match(n)
                .returning(Cypher.count(n).as("count"), Cypher.labels(n).as("labels"))
                .build();
        var renderedStatement = Renderer.getDefaultRenderer().render(statement);

        try (Session session = driver.session()) {
            return session.run(renderedStatement)
                    .list(record -> {
                        Set<String> labels = record.get("labels").asList().stream().map(Object::toString).collect(Collectors.toSet());
                        Integer count = record.get("count").asInt();
                        CountResult countResult = new CountResult();
                        countResult.setLabel(labels.stream().findFirst().orElse(""));
                        countResult.setCount(count);
                        return countResult;
                    });//.forEach(res -> result.put(res.getLabels().stream().findFirst().orElse(""), res.getCount()));
        }
    }

    private Entry mapToEntry(Node node) {
        Entry entry = new Entry();
        entry.setElementId(node.elementId());
        node.labels().forEach(label -> entry.getLabels().add(label));
        node.keys().forEach(key -> entry.getProperties().put(key, nullEscaped(node.get(key).asString())));
        return entry;
    }

    private String nullEscaped(String string) {
        return "null".equals(string) ? null : string;
    }

}

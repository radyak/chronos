package net.fvogel.chronos.data.service;

import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.data.model.CountResult;
import net.fvogel.chronos.data.model.DataElement;
import net.fvogel.chronos.data.model.Query;
import net.fvogel.chronos.data.model.SortOrder;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.SortItem;
import org.neo4j.cypherdsl.core.renderer.Renderer;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
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

    @Autowired
    private Driver driver;

    public List<DataElement> findAll(Query query) {
        var nodeName = "n";
        var n = Cypher.anyNode().named(nodeName);

        var sortList = new ArrayList<SortItem>();
        if (query.getSortBy() != null) {
            var direction = query.getSortOrder() == SortOrder.ASC ? SortItem.Direction.ASC : SortItem.Direction.DESC;
            var property = query.getSortBy();
            var sort = Cypher.sort(n.property(property), direction);
            sortList.add(sort);
        }

        var statement = Cypher.match(n)
                .returning(n)
                .orderBy(sortList)
                .skip((query.getPage() - 1) * query.getPageSize())
                .limit(query.getPageSize())
                .build();
        var renderedStatement = Renderer.getDefaultRenderer().render(statement);

        try (Session session = driver.session()) {
            return session.run(renderedStatement)
                    .list(record -> record.get(nodeName).asNode())
                    .stream().map(this::mapToEntry)
                    .collect(Collectors.toList());
        }
    }

    public DataElement findById(String id) {
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

    private DataElement mapToEntry(Node node) {
        DataElement entry = new DataElement();
        entry.setElementId(node.elementId());
        node.labels().forEach(label -> entry.getLabels().add(label));
        node.keys().forEach(key -> entry.getProperties().put(key, nullEscaped(node.get(key).asString())));
        return entry;
    }

    private String nullEscaped(String string) {
        return "null".equals(string) ? null : string;
    }

}

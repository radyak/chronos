package net.fvogel.chronos.data.service;

import net.fvogel.chronos.data.model.CountResult;
import net.fvogel.chronos.data.model.DataQuery;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.Pagination;
import net.fvogel.chronos.data.persistence.CypherClient;
import org.neo4j.cypherdsl.core.Condition;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.SortItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    private CypherClient client;

    @Autowired
    private SecurityService securityService;

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

    public void create(Entry entry) {
        // TODO: Schema validation
        entry.get_meta().setCreateAuthor(securityService.getUsername());

        var n = entryMapper.toNode(entry, "n");
        var statement = Cypher.create(n).returning(n).build();

        client.runStatement(statement);
    }

}

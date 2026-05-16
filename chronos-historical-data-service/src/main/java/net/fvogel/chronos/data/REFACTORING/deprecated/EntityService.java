package net.fvogel.chronos.data.REFACTORING.deprecated;

import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.data.REFACTORING.reuse.CreateNodeQuery;
import net.fvogel.chronos.data.REFACTORING.reuse.DeleteNodeQuery;
import net.fvogel.chronos.data.REFACTORING.reuse.QueryService;
import net.fvogel.chronos.data.REFACTORING.reuse.UpdateNodeQuery;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static net.fvogel.chronos.data.REFACTORING.reuse.QueryUtils.wrapWith;


@Service
@Transactional
@Deprecated
public class EntityService {

    private static final Logger logger = LoggerFactory.getLogger(EntityService.class);

    private final Driver driver;
    private final QueryService queryService;

    public EntityService(Driver driver,
                         QueryService queryService) {
        this.driver = driver;
        this.queryService = queryService;
    }

    public LegacyEntity findRandomEntityWithQid() {
        try (Session session = driver.session()) {
            return session.run("MATCH (n) WHERE n.qid IS NOT null RETURN n, rand() as r ORDER BY r LIMIT 1")
                    .list(record -> record.get("n").asNode())
                    .stream().map(this::mapToLabelledEntity).findFirst()
                    .orElseThrow(NotFoundException::new);
        }
    }

    public LegacyEntity findById(String id) {
        try (Session session = driver.session()) {
            return session.run("MATCH (n) WHERE n.id = " + wrapWith(id, "'") + " RETURN n")
                    .list(record -> record.get("n").asNode())
                    .stream().map(this::mapToEntity).findFirst()
                    .orElseThrow(NotFoundException::new);
        }
    }

    public Set<Map<String, Object>> findWithRelationsById(String id) {
        try (Session session = driver.session()) {
            return session.run("MATCH (s)-[r]->(t) WHERE s.id = " + wrapWith(id, "'") + " RETURN s, r, t")
                    .list(record -> {
                        return new HashSet<>(Arrays.asList(
                                getNode("s", record),
                                getRelationship("r", record),
                                getNode("t", record))
                        );
                    })
                    .stream().findFirst()
                    .orElseThrow(NotFoundException::new);
        }
    }

    private Map<String, Object> getNode(String key, org.neo4j.driver.Record record) {
        Node node = record.get(key).asNode();
        Map<String, Object> map = new HashMap<>(node.asMap());
        map.put("labels", toList(node.labels()));
        map.put("_id", node.elementId());
        return map;
    }

    private Map<String, Object> getRelationship(String key, org.neo4j.driver.Record record) {
        Relationship relationship = record.get(key).asRelationship();
        Map<String, Object> map = new HashMap<>(relationship.asMap());
        map.put("type", relationship.type());
        map.put("source", relationship.startNodeElementId());
        map.put("target", relationship.endNodeElementId());
        map.put("_id", relationship.elementId());
        return map;
    }

    private <T> List<T> toList(Iterable<T> it) {
        List<T> list = new ArrayList<T>();
        it.forEach(list::add);
        return list;
    }

    public List<LegacyEntity> findAll() {
        try (Session session = driver.session()) {
            return session.run("MATCH (n) LIMIT 50 RETURN n")
                    .list(record -> record.get("n").asNode())
                    .stream().map(this::mapToEntity).toList();
        }
    }

    public LegacyEntity createEntity(String type, LegacyEntity legacyEntity) {
        legacyEntity.id = UUID.randomUUID().toString();
        CreateNodeQuery query = queryService.createNodeQuery(type, legacyEntity);
        try (Session session = driver.session()) {
            String createQuery = query.toString();
            logger.info("Executing create query: {}", createQuery);
            session.run(createQuery).single();
            return this.findById(legacyEntity.id);
        }
    }

    public LegacyEntity updateEntity(String type, String id, LegacyEntity legacyEntity) {
        // TODO: Add check for label
        LegacyEntity existing = this.findById(id);

        // Do not override ID
        legacyEntity.id = existing.id;
        UpdateNodeQuery query = queryService.updateNodeQuery(type, legacyEntity);
        try (Session session = driver.session()) {
            String updateQuery = query.toString();
            logger.info("Executing update query: {}", updateQuery);
            session.run(updateQuery).single();
            return this.findById(legacyEntity.id);
        }
    }

    public void deleteEntity(String type, String id) {
        LegacyEntity existing = this.findById(id);

        DeleteNodeQuery query = queryService.deleteNodeQuery(type, id);
        try (Session session = driver.session()) {
            String deleteQuery = query.toString();
            logger.info("Executing delete query: {}", deleteQuery);
            session.run(deleteQuery);
        }
    }

    private LegacyEntity mapToEntity(Node node) {
        LegacyEntity legacyEntity = new LegacyEntity();
        legacyEntity.id = nullEscaped(node.get("id").asString());
        legacyEntity.key = nullEscaped(node.get("key").asString());
        legacyEntity.from = nullEscaped(node.get("from").asString());
        legacyEntity.to = nullEscaped(node.get("to").asString());
        legacyEntity.qid = nullEscaped(node.get("qid").asString());
        return legacyEntity;
    }

    private String nullEscaped(String string) {
        return "null".equals(string) ? null : string;
    }

    private LegacyEntity mapToLabelledEntity(Node node) {
        LabelledLegacyEntity entity = new LabelledLegacyEntity();
        entity.id = nullEscaped(node.get("id").asString());
        entity.key = nullEscaped(node.get("key").asString());
        entity.from = nullEscaped(node.get("from").asString());
        entity.to = nullEscaped(node.get("to").asString());
        entity.qid = nullEscaped(node.get("qid").asString());

        Set<String> labels = new HashSet<>();
        node.labels().forEach(labels::add);
        entity.labels = labels;
        return entity;
    }

}

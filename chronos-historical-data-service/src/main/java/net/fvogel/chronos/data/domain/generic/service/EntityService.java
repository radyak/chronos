package net.fvogel.chronos.data.domain.generic.service;

import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.data.domain.generic.integration.QueryService;
import net.fvogel.chronos.data.domain.generic.persistence.Entity;
import net.fvogel.chronos.data.domain.generic.persistence.LabelledEntity;
import net.fvogel.chronos.data.domain.persistence.query.node.CreateNodeQuery;
import net.fvogel.chronos.data.domain.persistence.query.node.DeleteNodeQuery;
import net.fvogel.chronos.data.domain.persistence.query.node.UpdateNodeQuery;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static net.fvogel.chronos.data.domain.persistence.QueryUtils.wrapWith;


@Service
@Transactional
public class EntityService {

    private static final Logger logger = LoggerFactory.getLogger(EntityService.class);

    private final Driver driver;
    private final QueryService queryService;

    public EntityService(Driver driver,
                         QueryService queryService) {
        this.driver = driver;
        this.queryService = queryService;
    }

    public Entity findRandomEntityWithQid() {
        try (Session session = driver.session()) {
            return session.run("MATCH (n) WHERE n.qid IS NOT null RETURN n, rand() as r ORDER BY r LIMIT 1")
                    .list(record -> record.get("n").asNode())
                    .stream().map(this::mapToLabelledEntity).findFirst()
                    .orElseThrow(NotFoundException::new);
        }
    }

    public Entity findById(String id) {
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

    public List<Entity> findAll() {
        try (Session session = driver.session()) {
            return session.run("MATCH (n) LIMIT 50 RETURN n")
                    .list(record -> record.get("n").asNode())
                    .stream().map(this::mapToEntity).toList();
        }
    }

    public Entity createEntity(String type, Entity entity) {
        entity.id = UUID.randomUUID().toString();
        CreateNodeQuery query = queryService.createNodeQuery(type, entity);
        try (Session session = driver.session()) {
            String createQuery = query.toString();
            logger.info("Executing create query: {}", createQuery);
            session.run(createQuery).single();
            return this.findById(entity.id);
        }
    }

    public Entity updateEntity(String type, String id, Entity entity) {
        // TODO: Add check for label
        Entity existing = this.findById(id);

        // Do not override ID
        entity.id = existing.id;
        UpdateNodeQuery query = queryService.updateNodeQuery(type, entity);
        try (Session session = driver.session()) {
            String updateQuery = query.toString();
            logger.info("Executing update query: {}", updateQuery);
            session.run(updateQuery).single();
            return this.findById(entity.id);
        }
    }

    public void deleteEntity(String type, String id) {
        Entity existing = this.findById(id);

        DeleteNodeQuery query = queryService.deleteNodeQuery(type, id);
        try (Session session = driver.session()) {
            String deleteQuery = query.toString();
            logger.info("Executing delete query: {}", deleteQuery);
            session.run(deleteQuery);
        }
    }

    private Entity mapToEntity(Node node) {
        Entity entity = new Entity();
        entity.id = nullEscaped(node.get("id").asString());
        entity.key = nullEscaped(node.get("key").asString());
        entity.from = nullEscaped(node.get("from").asString());
        entity.to = nullEscaped(node.get("to").asString());
        entity.qid = nullEscaped(node.get("qid").asString());
        return entity;
    }

    private String nullEscaped(String string) {
        return "null".equals(string) ? null : string;
    }

    private Entity mapToLabelledEntity(Node node) {
        LabelledEntity entity = new LabelledEntity();
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

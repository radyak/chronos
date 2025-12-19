package net.fvogel.chronosbackend.domain.generic.service;

import net.fvogel.chronosbackend.domain.generic.integration.QueryService;
import net.fvogel.chronosbackend.domain.generic.integration.query.node.CreateNodeQuery;
import net.fvogel.chronosbackend.domain.generic.integration.query.node.DeleteNodeQuery;
import net.fvogel.chronosbackend.domain.generic.integration.query.node.UpdateNodeQuery;
import net.fvogel.chronosbackend.domain.generic.persistence.Entity;
import net.fvogel.chronosbackend.domain.generic.persistence.LabelledEntity;
import net.fvogel.chronosbackend.shared.exception.NotFoundException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static net.fvogel.chronosbackend.domain.generic.integration.QueryUtils.wrapWith;


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

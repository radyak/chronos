package net.fvogel.chronosbackend.domain.generic.service;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class EntityService {

    private static final Logger logger = LoggerFactory.getLogger(EntityService.class);

    private final Driver driver;

    public EntityService(Driver driver) {
        this.driver = driver;
    }

    public List<Entity> findAllNodes() {
        try (Session session = driver.session()) {
            return session.run("MATCH (n) RETURN n LIMIT 50")
                    .list(record -> record.get("n").asNode())
                    .stream().map(this::mapToLabelledEntity).toList();
        }
    }

    public Entity findRandomEntity() {
        try (Session session = driver.session()) {
            return session.run("MATCH (n) WHERE n.qid IS NOT null RETURN n, rand() as r ORDER BY r LIMIT 1")
                    .list(record -> record.get("n").asNode())
                    .stream().map(this::mapToLabelledEntity).findFirst()
                    .orElseThrow(NotFoundException::new);
        }
    }

    private Entity mapToLabelledEntity(Node node) {
        LabelledEntity entity = new LabelledEntity();
        entity.setId(node.get("id").asString());
        entity.setKey(node.get("key").asString());
        entity.setFrom(node.get("from").asString());
        entity.setTo(node.get("to").asString());
        entity.setQid(node.get("qid").asString());

        Set<String> labels = new HashSet<>();
        node.labels().forEach(labels::add);
        entity.setLabels(labels);
        return entity;
    }

}

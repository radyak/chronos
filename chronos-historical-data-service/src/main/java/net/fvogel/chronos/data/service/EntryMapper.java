package net.fvogel.chronos.data.service;

import net.fvogel.chronos.commons.exception.InvalidDataException;
import net.fvogel.chronos.data.model.CountResult;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.Relation;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.types.InternalTypeSystem;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Split and clean up
@Service
public class EntryMapper {

    private static final Logger logger = LoggerFactory.getLogger(EntryMapper.class);

    public Entry toEntry(Node node) {
        Entry entry = new Entry();
        entry.setElementId(node.elementId());
        node.labels().forEach(label -> entry.getLabels().add(label));
        node.keys().forEach(key -> {
            // Meta info fields start with underscore "_"
            if (key.startsWith("_")) {
                return;
            }
            entry.getAttributes().put(key, value(node.get(key)));
        });
        entry.get_meta().setVersion(node.get("_version").asInt(1));
        entry.get_meta().setCreateAuthor(node.get("_createAuthor").asString(null));
        entry.get_meta().setCreateDate(node.get("_createDate").asString(null));
        entry.get_meta().setLastUpdateAuthor(node.get("_lastUpdateAuthor").asString(null));
        entry.get_meta().setLastUpdateDate(node.get("_lastUpdateDate").asString(null));
        return entry;
    }

    public Relation toRelation(Relationship relationship) {
        Relation relation = new Relation();
        relation.setElementId(relationship.elementId());
        relation.setType(relationship.type());
        relationship.keys().forEach(key -> {
            // Meta info fields start with underscore "_"
            if (key.startsWith("_")) {
                return;
            }
            relation.getAttributes().put(key, value(relationship.get(key)));
        });
        relation.setStartElementId(relationship.startNodeElementId());
        relation.setEndElementId(relationship.endNodeElementId());
        relation.get_meta().setVersion(relationship.get("_version").asInt(1));
        relation.get_meta().setCreateAuthor(relationship.get("_createAuthor").asString(null));
        relation.get_meta().setCreateDate(relationship.get("_createDate").asString(null));
        relation.get_meta().setLastUpdateAuthor(relationship.get("_lastUpdateAuthor").asString(null));
        relation.get_meta().setLastUpdateDate(relationship.get("_lastUpdateDate").asString(null));
        return relation;
    }

    public org.neo4j.cypherdsl.core.Node toNode(Entry entry, String name) {
        var label = entry.getLabels().stream().findFirst().orElseThrow(InvalidDataException::new);
        Map<String, Object> properties = new HashMap<>(entry.getAttributes());

        mapMetaUpdates(properties, entry);

        return Cypher.node(label).named(name).withProperties(properties);
    }

    public void mapMetaUpdates(Map<String, Object> properties, Entry entry) {
        properties.put("_version", entry.get_meta().getVersion());
        properties.put("_createAuthor", entry.get_meta().getCreateAuthor());
        properties.put("_createDate", entry.get_meta().getCreateDate());
        properties.put("_lastUpdateAuthor", entry.get_meta().getLastUpdateAuthor());
        properties.put("_lastUpdateDate", entry.get_meta().getLastUpdateDate());
    }

    public CountResult toCountResult(Record record) {
        Set<String> labels = record.get("labels").asList().stream().map(Object::toString).collect(Collectors.toSet());
        Integer count = Integer.valueOf(record.get("count").asInt());
        CountResult countResult = new CountResult();
        countResult.setLabel(labels.stream().findFirst().orElse(""));
        countResult.setCount(count);
        return countResult;
    }

    private Object value(Value value) {
        if (value.isNull()) {
            return null;
        }
        switch (value.type().name()) {
            case "STRING":
                return value.asString();
            case "BOOLEAN":
                return value.asBoolean();
            case "INTEGER":
                return value.asInt();
            case "FLOAT":
                return value.asFloat();
            case "NUMBER":
                return value.asNumber();
        }
        if (value.hasType(InternalTypeSystem.TYPE_SYSTEM.LIST())) {
            return value.asList();
        }

        return null;
    }

}

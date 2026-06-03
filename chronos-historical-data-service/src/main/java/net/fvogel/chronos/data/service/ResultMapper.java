package net.fvogel.chronos.data.service;

import net.fvogel.chronos.data.model.CountResult;
import net.fvogel.chronos.data.model.Entry;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.types.InternalTypeSystem;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResultMapper {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapper.class);

    public Entry toEntry(Node node) {
        Entry entry = new Entry();
        entry.setElementId(node.elementId());
        node.labels().forEach(label -> entry.getLabels().add(label));
        node.keys().forEach(key -> entry.getAttributes().put(key, value(node.get(key))));
        return entry;
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

package net.fvogel.chronos.data.service;

import net.fvogel.chronos.data.model.CountResult;
import net.fvogel.chronos.data.model.Entry;
import org.neo4j.driver.Record;
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
        node.keys().forEach(key -> entry.getProperties().put(key, nullEscaped(node.get(key).asString())));
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

    private String nullEscaped(String string) {
        return "null".equals(string) ? null : string;
    }

}

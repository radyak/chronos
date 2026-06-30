package net.fvogel.chronos.data.model.internal;

import lombok.Data;

import java.util.*;

@Data
public class Entry {
    String elementId;
    Set<String> labels = new HashSet<>();
    Map<String, Object> attributes = new HashMap<>();
    MetaInfo _meta = new MetaInfo();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return Objects.equals(elementId, entry.elementId) && Objects.equals(attributes.get("key"), entry.attributes.get("key"));
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementId, attributes);
    }
}

package net.fvogel.chronos.data.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class Relation {
    String elementId;
    String startElementId;
    String endElementId;
    String type;
    Map<String, Object> attributes = new HashMap<>();
    MetaInfo _meta = new MetaInfo();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Relation relation = (Relation) o;
        return Objects.equals(elementId, relation.elementId) &&
                Objects.equals(startElementId, relation.startElementId) &&
                Objects.equals(endElementId, relation.endElementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementId, startElementId, endElementId);
    }
}

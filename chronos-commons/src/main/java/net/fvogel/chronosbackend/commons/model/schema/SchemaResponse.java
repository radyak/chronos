package net.fvogel.chronosbackend.commons.model.schema;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SchemaResponse {
    SchemaResponseMetaInfoDTO meta = new SchemaResponseMetaInfoDTO();
    Set<Entity> entities = new HashSet<>();
    Set<Relation> relations = new HashSet<>();

    @Data
    public static class SchemaResponseMetaInfoDTO {
        Integer depth;
        String query;
        String base;
    }
}

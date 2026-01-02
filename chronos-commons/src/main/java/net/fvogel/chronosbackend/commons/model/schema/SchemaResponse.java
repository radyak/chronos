package net.fvogel.chronosbackend.commons.model.schema;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SchemaResponse {
    SchemaResponseMetaInfoDTO meta = new SchemaResponseMetaInfoDTO();
    EntitiesInfoDTO entities = new EntitiesInfoDTO();
    RelationsInfoDTO relations = new RelationsInfoDTO();

    @Data
    public static class SchemaResponseMetaInfoDTO {
        Integer depth;
        String query;
        String base;
    }

    @Data
    public static class EntitiesInfoDTO {
        Set<Entity> elements = new HashSet<>();
        Set<Attribute> defaultAttributes = new HashSet<>();
    }

    @Data
    public static class RelationsInfoDTO {
        Set<Relation> elements = new HashSet<>();
        Set<Attribute> defaultAttributes = new HashSet<>();
    }

}

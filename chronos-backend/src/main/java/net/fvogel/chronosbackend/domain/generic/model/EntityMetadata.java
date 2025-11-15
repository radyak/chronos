package net.fvogel.chronosbackend.domain.generic.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EntityMetadata {
    String name;
    List<EntityFieldMetadata> fields = new ArrayList<>();
    List<RelationMetadata> relations = new ArrayList<>();
}

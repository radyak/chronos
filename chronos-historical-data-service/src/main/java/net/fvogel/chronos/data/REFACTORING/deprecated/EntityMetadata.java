package net.fvogel.chronos.data.REFACTORING.deprecated;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Deprecated
public class EntityMetadata {
    String name;
    List<EntityFieldMetadata> fields = new ArrayList<>();
    List<RelationMetadata> relations = new ArrayList<>();
}

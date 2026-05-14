package net.fvogel.chronos.data.REFACTORING.deprecated;

import lombok.Data;

@Data
@Deprecated
public class RelationMetadata {
    String entityField;
    String relationClass;
    String relationName;
    Direction direction;
    String tagetEntity;

    public enum Direction {
        IN,
        OUT
    }
}

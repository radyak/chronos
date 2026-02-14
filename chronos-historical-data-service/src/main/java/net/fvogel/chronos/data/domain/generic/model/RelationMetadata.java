package net.fvogel.chronos.data.domain.generic.model;

import lombok.Data;

@Data
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

package net.fvogel.chronosbackend.relationships;

import lombok.Data;
import net.fvogel.chronosbackend.models.Territory;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.util.Set;


@RelationshipProperties
@Data
public class Ruled {

    @RelationshipId
    private Long id;

    String from;
    String to;
    Set<String> titles;

    @TargetNode
    Territory territory;
}

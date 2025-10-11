package net.fvogel.chronosbackend.models;

import lombok.Data;
import net.fvogel.chronosbackend.relationships.Ruled;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.List;

@Node("Territory")
@Data
public class Territory {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    String id;

    String key;

    String from;
    String to;

    String name;

    @Relationship(type = "RULED", direction = Relationship.Direction.INCOMING)
    private List<Ruled> ruledBy;
}

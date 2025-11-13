package net.fvogel.chronosbackend.domain.person.persistence;

import lombok.Data;
import net.fvogel.chronosbackend.domain.generic.persistence.Entity;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Person")
@Data
public class Person extends Entity {

    @Relationship(type = "CHILD_OF", direction = Relationship.Direction.OUTGOING)
    private List<ChildOf> parents;

}

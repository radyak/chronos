package net.fvogel.chronosbackend.domain.person.persistence;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.fvogel.chronosbackend.domain.generic.persistence.Entity;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Node("Person")
@Data
@EqualsAndHashCode(callSuper = true)
public class Person extends Entity {


//    @Relationship(type = "RULED", direction = Relationship.Direction.OUTGOING)
//    private List<Ruled> ruled;

}

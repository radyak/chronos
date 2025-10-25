package net.fvogel.chronosbackend.common.persistence.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Node("Person")
@Data
public class Person {

    @Id
    // TODO: Improve to DB-generated value
    @GeneratedValue(UUIDStringGenerator.class)
    String id;

    String key;

    String from;
    String to;

    String name;

//    @Relationship(type = "RULED", direction = Relationship.Direction.OUTGOING)
//    private List<Ruled> ruled;

}

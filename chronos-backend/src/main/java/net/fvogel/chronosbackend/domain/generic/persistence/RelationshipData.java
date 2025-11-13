package net.fvogel.chronosbackend.domain.generic.persistence;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;


@Data
public class RelationshipData {

    @Id
    @GeneratedValue
    String id;

    String from;
    String to;
}

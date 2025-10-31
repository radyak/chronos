package net.fvogel.chronosbackend.domain.generic.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entity {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    String id;

    String key;
    String from;
    String to;
    String qid;
}

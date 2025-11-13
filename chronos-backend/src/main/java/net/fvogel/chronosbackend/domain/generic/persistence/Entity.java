package net.fvogel.chronosbackend.domain.generic.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entity {

    @Id
    String id = UUID.randomUUID().toString();

    String key;
    String from;
    String to;
    String qid;
}

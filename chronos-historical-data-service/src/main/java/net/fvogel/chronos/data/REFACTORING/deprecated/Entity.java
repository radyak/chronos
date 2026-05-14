package net.fvogel.chronos.data.REFACTORING.deprecated;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class Entity {

    @Id
    public String id = UUID.randomUUID().toString();

    public String key;

    public String from;

    public String to;

    public String qid;
}

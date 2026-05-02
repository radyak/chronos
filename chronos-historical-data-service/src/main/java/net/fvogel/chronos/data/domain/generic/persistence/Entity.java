package net.fvogel.chronos.data.domain.generic.persistence;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.fvogel.chronos.data.domain.common.validation.ChronosDateSpec;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
public class Entity {

    @Id
    public String id = UUID.randomUUID().toString();

    public String key;

    @ChronosDateSpec
    public String from;

    @ChronosDateSpec
    public String to;

    public String qid;
}

package net.fvogel.chronosbackend.domain.generic.persistence;

import net.fvogel.chronosbackend.domain.common.ChronosDateSpec;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;


public class RelationshipData {

    @Id
    @GeneratedValue
    public String id;

    @ChronosDateSpec
    public String from;

    @ChronosDateSpec
    public String to;
}

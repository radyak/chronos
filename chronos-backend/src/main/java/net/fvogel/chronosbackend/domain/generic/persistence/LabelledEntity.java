package net.fvogel.chronosbackend.domain.generic.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelledEntity extends Entity {
    Set<String> labels;
}

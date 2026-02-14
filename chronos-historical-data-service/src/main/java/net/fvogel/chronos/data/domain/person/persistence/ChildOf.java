package net.fvogel.chronos.data.domain.person.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fvogel.chronos.data.domain.generic.persistence.RelationshipData;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildOf extends RelationshipData {

//    @Id
//    @GeneratedValue
//    private String id;

    @TargetNode
    private Person parent;

//    private String from;
//    private String to;

    private ChildType type;

    public enum ChildType {
        BIOLOGICAL,
        ADOPTED
    }

}

package net.fvogel.chronosbackend.common.persistence.relations.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;

@Entity
@Table(name = "relation")
@Data
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="relation_type_id", nullable = false)
    private RelationType type;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="entry_from_id", nullable = false)
    private Entry from;

    @Column(name="entry_from_id", insertable=false, updatable=false)
    private Long fromId;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="entry_to_id", nullable = false)
    private Entry to;

    @Column(name="entry_to_id", insertable=false, updatable=false)
    private Long toId;

    @Column(name = "rel_value")
    private String value;

}

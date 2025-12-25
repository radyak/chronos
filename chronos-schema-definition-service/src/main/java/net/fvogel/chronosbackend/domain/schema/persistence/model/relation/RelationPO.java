package net.fvogel.chronosbackend.domain.schema.persistence.model.relation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class RelationPO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    /**
     * The actual key used for the schema.
     */
    @Column(name = "technical_key", unique = true, nullable = false)
    @NotNull
    String key;

    /**
     * A label or label key used for human-readable purposes, such as the UI
     */
    @Column(unique = true, nullable = false)
    @NotNull
    String label;

    String explanation;

    String examples;

    @OneToMany
    List<RelationAttributePO> attributes = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "source_entity_id", nullable = false)
    EntityPO source;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_entity_id", nullable = false)
    EntityPO target;

}

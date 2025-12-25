package net.fvogel.chronosbackend.domain.schema.persistence.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationPO;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class EntityPO {

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
    List<EntityAttributePO> attributes = new ArrayList<>();

    @OneToMany(mappedBy = "source")
    List<RelationPO> relationPOS = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY)
    List<RelationPO> inboundRelationPOS = new ArrayList<>();

}

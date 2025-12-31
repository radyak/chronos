package net.fvogel.chronosbackend.domain.schema.persistence.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.fvogel.chronosbackend.config.i18n.I18nConstants;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationPO;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @NotNull(message = I18nConstants.Errors.Entities.KEY_NOT_SPECIFIED)
    String key;

    /**
     * A label or label key used for human-readable purposes, such as the UI
     */
    @Column(unique = true, nullable = false)
    @NotNull(message = I18nConstants.Errors.Entities.LABEL_NOT_SPECIFIED)
    String label;

    String explanation;

    String examples;

    @OneToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "entity_id")
    @Valid
    List<EntityAttributePO> attributes = new ArrayList<>();

    @OneToMany(mappedBy = "source", orphanRemoval = true, cascade = CascadeType.REMOVE)
    // CascadeType.REMOVE targets to handle relations together with entities, not separately; it should be evaluated later on,
    // which behavior is more desirable
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Valid
    List<RelationPO> relations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<RelationPO> inboundRelations = new ArrayList<>();

}

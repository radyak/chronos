package net.fvogel.chronosbackend.domain.schema.persistence.model.relation;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.fvogel.chronosbackend.config.i18n.I18nConstants;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"source", "target"})
@Entity
public class RelationPO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    /**
     * The actual key used for the schema.
     */
    @Column(name = "technical_key", nullable = false, length = 64)
    @Size(min = 3, max = 64, message = I18nConstants.Errors.INVALID_LENGTH)
    @NotNull(message = I18nConstants.Errors.NOT_SPECIFIED)
    String key;

    @Size(min = 3, max = 255, message = I18nConstants.Errors.INVALID_LENGTH)
    String explanation;

    @Size(min = 3, max = 255, message = I18nConstants.Errors.INVALID_LENGTH)
    String examples;

    @OneToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "relation_id")
    @Valid
    List<RelationAttributePO> attributes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "source_entity_id", nullable = false)
    EntityPO source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_entity_id", nullable = false)
    EntityPO target;

}

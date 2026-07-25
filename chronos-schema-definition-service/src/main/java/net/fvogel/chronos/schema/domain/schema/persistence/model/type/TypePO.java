package net.fvogel.chronos.schema.domain.schema.persistence.model.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.fvogel.chronos.schema.config.i18n.I18nConstants;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class TypePO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    /**
     * The actual key used for the schema.
     */
    @Column(name = "technical_key", unique = true, nullable = false, length = 64)
    @Size(min = 3, max = 64, message = I18nConstants.Errors.INVALID_LENGTH)
    @NotNull(message = I18nConstants.Errors.NOT_SPECIFIED)
    String key;

    @Size(min = 3, max = 255, message = I18nConstants.Errors.INVALID_LENGTH)
    String explanation;

    @Size(min = 3, max = 255, message = I18nConstants.Errors.INVALID_LENGTH)
    String examples;

    String icon;

    String color;

    @OneToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "type_id")
    @Valid
    List<TypeAttributePO> attributes = new ArrayList<>();

    @OneToMany(mappedBy = "source", orphanRemoval = true, cascade = CascadeType.REMOVE)
    // CascadeType.REMOVE targets to handle relations together with types, not separately; it should be evaluated later on,
    // which behavior is more desirable
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Valid
    List<RelationPO> relations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<RelationPO> inboundRelations = new ArrayList<>();

}

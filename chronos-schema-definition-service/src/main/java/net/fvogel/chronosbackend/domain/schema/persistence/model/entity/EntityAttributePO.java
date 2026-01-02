package net.fvogel.chronosbackend.domain.schema.persistence.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.fvogel.chronosbackend.commons.model.schema.AttributeType;
import net.fvogel.chronosbackend.config.i18n.I18nConstants;
import net.fvogel.chronosbackend.shared.persistence.StringSetConverter;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class EntityAttributePO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    /**
     * The actual key used for the schema.
     */
    @Column(name = "technical_key", nullable = false)
    @NotNull(message = I18nConstants.Errors.Entities.Attributes.KEY_NOT_SPECIFIED)
    String key;

    /**
     * A label or label key used for human-readable purposes, such as the UI
     */
    @Column(unique = true, nullable = false)
    @NotNull(message = I18nConstants.Errors.Entities.Attributes.LABEL_NOT_SPECIFIED)
    String label;

    String explanation;

    String examples;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = I18nConstants.Errors.Entities.Attributes.TYPE_NOT_SPECIFIED)
    AttributeType type;

    Boolean isArray = false;

    Boolean isMandatory = false;

    /**
     * Optional restriction for type=STRING
     * Defines the pattern of the attribute values
     */
    String valuePattern;

    /**
     * Optional restriction for type=NUMBER
     * Defines the range of the attribute values
     * <p>
     * Format: [/]{lower bound; optional},{upper bound; optional}]/[
     * Examples:
     * [0,300]     -   Min: 0, Max: 300
     * ]-10,10]    -   Min: -9 (-10 is excluded), Max: 10
     * [,100[      -   Min: no minimum, Max: 99 (100 excluded)
     */
    String valueRange;

    /**
     * Optional restriction for type=ENUM
     * Defines discrete values of the attribute values
     */
    @Convert(converter = StringSetConverter.class)
    Set<String> allowedValues = new HashSet<>();

}

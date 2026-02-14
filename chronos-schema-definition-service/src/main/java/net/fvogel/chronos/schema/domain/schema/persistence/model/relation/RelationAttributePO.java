package net.fvogel.chronos.schema.domain.schema.persistence.model.relation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.fvogel.chronos.commons.model.schema.AttributeType;
import net.fvogel.chronos.schema.config.i18n.I18nConstants;
import net.fvogel.chronos.schema.shared.persistence.StringSetConverter;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class RelationAttributePO {

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

    @Column(length = 255)
    @Size(min = 3, max = 255, message = I18nConstants.Errors.INVALID_LENGTH)
    String explanation;

    @Column(length = 255)
    @Size(min = 3, max = 255, message = I18nConstants.Errors.INVALID_LENGTH)
    String examples;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = I18nConstants.Errors.NOT_SPECIFIED)
    AttributeType type;

    Boolean isArray = false;

    Boolean isMandatory = false;

    /**
     * Optional restriction for type=STRING
     * Defines the pattern of the attribute values
     */
    @Column(length = 255)
    @Size(min = 3, max = 255, message = I18nConstants.Errors.INVALID_LENGTH)
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
    @Column(length = 255)
    @Size(min = 3, max = 255, message = I18nConstants.Errors.INVALID_LENGTH)
    String valueRange;

    /**
     * Optional restriction for type=ENUM
     * Defines discrete values of the attribute values
     */
    @Column(length = 1024)
    @Size(max = 64)
    @Convert(converter = StringSetConverter.class)
    Set<String> allowedValues = new HashSet<>();

}

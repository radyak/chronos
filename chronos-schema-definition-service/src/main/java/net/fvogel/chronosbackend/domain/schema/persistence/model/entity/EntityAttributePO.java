package net.fvogel.chronosbackend.domain.schema.persistence.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.fvogel.chronosbackend.commons.model.schema.AttributeType;
import net.fvogel.chronosbackend.shared.persistence.StringListConverter;

import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    AttributeType type;

    Boolean isArray = false;

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
    @Convert(converter = StringListConverter.class)
    List<String> allowedValues = new ArrayList<>();

}

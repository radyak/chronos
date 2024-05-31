package net.fvogel.chronosbackend.common.persistence.relations.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "relation_type")
@Data
public class RelationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "name_label", nullable = false)
    private String nameLabel;

    @NotNull
    @Column(name = "name_inverse_label", nullable = false)
    private String nameInverseLabel;

    @Column(name = "value_label")
    private String valueLabel;

}

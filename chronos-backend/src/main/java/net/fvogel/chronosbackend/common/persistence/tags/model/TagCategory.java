package net.fvogel.chronosbackend.common.persistence.tags.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "tag_category")
@Data
public class TagCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 32)
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Size(min = 2, max = 32)
    @Column(name = "icon")
    private String icon;

}

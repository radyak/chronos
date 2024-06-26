package net.fvogel.chronosbackend.common.persistence.tags.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tag")
@Data
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 32)
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @NotBlank
    @Size(min = 3, max = 32)
    @Column(name = "color")
    private String color;

    @NotNull
    @ManyToOne
    @JoinColumn(name="tag_category_id")
    private TagCategory tagCategory;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private List<Entry> entries;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

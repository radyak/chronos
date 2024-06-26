package net.fvogel.chronosbackend.common.persistence.entries.model;

import jakarta.persistence.*;
import lombok.Data;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "entry")
@Data
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Column(name = "subtitle", unique = true, nullable = true)
    private String subTitle;

    @OneToMany(
            cascade= {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH,
                    CascadeType.REMOVE
            },
            orphanRemoval = true
    )
    @JoinColumn(name="entry_id")
    private List<DateRange> dateRanges = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "entry_tag",
        joinColumns = @JoinColumn(name = "entry_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    Set<Tag> tags = new HashSet<>();

    @Column(name = "wikipedia_page", nullable = true)
    private String wikipediaPage;

}

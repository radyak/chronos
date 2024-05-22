package net.fvogel.chronosbackend.common.persistence.tags.repo;

import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Set<Tag> findByIdIn(Collection<Long> tagIds);
}

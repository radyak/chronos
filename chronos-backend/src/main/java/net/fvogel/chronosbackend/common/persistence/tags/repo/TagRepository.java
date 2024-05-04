package net.fvogel.chronosbackend.common.persistence.tags.repo;

import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}

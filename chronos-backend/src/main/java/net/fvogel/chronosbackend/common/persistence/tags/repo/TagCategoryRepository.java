package net.fvogel.chronosbackend.common.persistence.tags.repo;

import net.fvogel.chronosbackend.common.persistence.tags.model.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagCategoryRepository extends JpaRepository<TagCategory, Long> {
}

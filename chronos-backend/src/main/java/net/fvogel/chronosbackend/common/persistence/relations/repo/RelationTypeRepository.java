package net.fvogel.chronosbackend.common.persistence.relations.repo;

import net.fvogel.chronosbackend.common.persistence.relations.model.RelationType;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface RelationTypeRepository extends JpaRepository<RelationType, Long> {
}

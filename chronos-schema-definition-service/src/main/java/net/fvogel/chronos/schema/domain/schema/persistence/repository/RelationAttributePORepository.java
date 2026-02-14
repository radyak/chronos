package net.fvogel.chronos.schema.domain.schema.persistence.repository;

import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationAttributePO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationAttributePORepository extends JpaRepository<RelationAttributePO, Long> {

}

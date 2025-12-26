package net.fvogel.chronosbackend.domain.schema.persistence.repository;

import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationAttributePO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationAttributePORepository extends JpaRepository<RelationAttributePO, Long> {

}

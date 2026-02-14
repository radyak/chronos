package net.fvogel.chronos.schema.domain.schema.persistence.repository;

import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RelationPORepository extends JpaRepository<RelationPO, Long> {

    Optional<RelationPO> findByKey(String name);

}

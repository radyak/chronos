package net.fvogel.chronosbackend.domain.schema.persistence.repository;

import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntityPORepository extends JpaRepository<EntityPO, Long> {

    Optional<EntityPO> findByKey(String key);

}

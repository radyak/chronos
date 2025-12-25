package net.fvogel.chronosbackend.domain.schema.persistence;

import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityAttributePO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityAttributePORepository extends JpaRepository<EntityAttributePO, Long> {

}

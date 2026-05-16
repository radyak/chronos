package net.fvogel.chronos.schema.domain.schema.persistence.repository;

import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypePORepository extends JpaRepository<TypePO, Long> {

    Optional<TypePO> findByKey(String key);

}

package net.fvogel.chronosbackend.common.persistence.relations.repo;

import net.fvogel.chronosbackend.common.persistence.relations.model.Relation;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface RelationRepository extends JpaRepository<Relation, Long> {

    @Query(
            """
            select r
            from Relation r
            where (
                r.fromId = :entryId
                OR
                r.toId = :entryId
            )
            """
    )
    Collection<Relation> findByEntryId(@Param("entryId") Long entryId);
}

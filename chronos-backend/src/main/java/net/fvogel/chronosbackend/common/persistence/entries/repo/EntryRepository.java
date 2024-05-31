package net.fvogel.chronosbackend.common.persistence.entries.repo;

import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    @Query("""
            select e
            from Entry e
            where (
                :ids IS NULL
                OR
                e.id IN :ids
            ) AND (
                lower(e.title) like lower(concat('%', :title,'%'))
                OR
                :title IS NULL
            ) AND (
                :tagIds IS NULL
                OR
                (SELECT COUNT(t) FROM e.tags t WHERE t.id IN :tagIds) = :#{#tagIds == null ? 0 : #tagIds.size()}
            ) AND (
                :from IS NULL
                OR
                ((SELECT COUNT(d) FROM e.dateRanges d WHERE d.end > :from) > 0)
            ) AND (
                :to IS NULL
                OR
                ((SELECT COUNT(d) FROM e.dateRanges d WHERE d.start < :to) > 0)
            )
            """
    )
    List<Entry> findBy(
            @Param("ids") Set<Long> ids,
            @Param("title") String title,
            @Param("tagIds") Set<Long> tagIds,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("select e.id from Entry e order by random()")
    List<Long> findRandom();

    Set<Entry> findByIdIn(Collection<Long> entryIds);
}

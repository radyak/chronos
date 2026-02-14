package net.fvogel.chronos.data.domain.person.persistence;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, String> {

    Optional<Person> findByIdEqualsOrKey(String id, String key);

    @Query("""
            MATCH (p:Person {id: $id})
            OPTIONAL MATCH (p)-[r]->(f:Person)
            RETURN p, collect(r), collect(f)
            """)
    Optional<Person> findById(String id);

    @Query("""
            MATCH (a:Person)-[r:CHILD_OF]->(b:Person)
            WHERE a.id = $personId AND b.id = $parentId
            DELETE r
            """)
    void removeChild(String personId, String parentId);
}

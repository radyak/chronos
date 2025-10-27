package net.fvogel.chronosbackend.domain.person.persistence;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, String> {

    @Query("MATCH (p:Person) WHERE p.id = $identifier OR p.key = $identifier RETURN p")
    Optional<Person> findByIdOrKey(@Param("identifier") String identifier);

}

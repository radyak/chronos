package net.fvogel.chronosbackend.domain.person.persistence;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, String> {
    Person findOneByKey(String key);

    @Query("MATCH (p:Person) WHERE p.from >= $from RETURN p AND p.to <= $to RETURN p")
    List<Person> findBetween(
            @Param("from") String from,
            @Param("to") String to
    );

}

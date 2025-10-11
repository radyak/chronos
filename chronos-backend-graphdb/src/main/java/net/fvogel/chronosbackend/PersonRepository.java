package net.fvogel.chronosbackend;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import net.fvogel.chronosbackend.models.Person;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, String> {
    Person findOneByKey(String key);

    @Query("MATCH (p:Person) WHERE p.from > $date RETURN p")
    List<Person> findPersonsBornAfter(@Param("date") String date);
}

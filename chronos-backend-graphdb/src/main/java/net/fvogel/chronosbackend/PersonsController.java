package net.fvogel.chronosbackend;

import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.web.bind.annotation.*;
import net.fvogel.chronosbackend.models.Person;

import java.util.List;

import static org.neo4j.cypherdsl.core.Cypher.*;

@RestController
@RequestMapping("/api/persons")
public class PersonsController {

    private final PersonRepository personRepository;
    private final Neo4jOperations template;

    public PersonsController(PersonRepository personRepository,
                             Neo4jOperations template) {
        this.personRepository = personRepository;
        this.template = template;
    }

    @GetMapping
    public List<Person> all() {
        return this.personRepository.findAll();
    }

    @GetMapping("/{key}")
    public Person findByKey(@PathVariable("key") String key) {
        return this.personRepository.findOneByKey(key);
    }

    /**
     * EXAMPLE with a Neo4jRepository
     */
    @GetMapping("/after")
    public List<Person> findPersonsBornAfter(@RequestParam("year") String year) {
        return this.personRepository.findPersonsBornAfter(year);
    }

    /**
     * EXAMPLE with Neo4jOperations
     */
    @GetMapping("/before")
    public List<Person> findPersonsDiedBefore(@RequestParam("year") String year) {
        // see https://neo4j.github.io/cypher-dsl/2025.0.3/
        var postNode = node("Person").named("p");
        var territoryNode = node("Territory").named("t");
        var relation = postNode.relationshipTo(territoryNode, "RULED").named("r");
        return this.template.findAll(
            match(relation)
                .where(postNode.property("to").lt(literalOf(year)))
                .returning(postNode, relation, territoryNode).build(),
            Person.class
        );
    }

    @PostMapping
    public Person create(@RequestBody Person person) {
        return this.personRepository.save(person);
    }

}

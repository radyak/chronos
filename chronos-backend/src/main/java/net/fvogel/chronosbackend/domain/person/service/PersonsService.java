package net.fvogel.chronosbackend.domain.person.service;

import net.fvogel.chronosbackend.domain.person.persistence.Person;
import net.fvogel.chronosbackend.domain.person.persistence.PersonRepository;
import net.fvogel.chronosbackend.shared.exception.InvalidDataException;
import net.fvogel.chronosbackend.shared.exception.InvalidParameterException;
import net.fvogel.chronosbackend.shared.exception.NotFoundException;
import org.neo4j.cypherdsl.core.Condition;
import org.neo4j.cypherdsl.core.StatementBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.neo4j.cypherdsl.core.Cypher.*;

@Service
@Transactional
public class PersonsService {

    private static final Logger logger = LoggerFactory.getLogger(PersonsService.class);

    private final PersonRepository personRepository;
    private final Neo4jOperations template;

    public PersonsService(PersonRepository personRepository,
                          Neo4jOperations template) {
        this.personRepository = personRepository;
        this.template = template;
    }

    public Person save(Person person) {
        try {
            return this.personRepository.save(person);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Error while saving Person " + person, e);
            throw new InvalidDataException();
        }
    }

    public List<Person> saveAll(Collection<Person> persons) {
        try {
            return this.personRepository.saveAll(persons);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Error while saving Persons " + persons, e);
            throw new InvalidDataException();
        }
    }

    public List<Person> findAll() {
        return this.personRepository.findAll();
    }

    public List<Person> findBetween(String from, String to) {
        var person = node("Person").named("p");

        List<Condition> conditions = new ArrayList<>();

        if (from != null) {
            conditions.add(person.property("from").gt(literalOf(from)));
        }
        if (to != null) {
            conditions.add(person.property("to").lt(literalOf(to)));
        }

        StatementBuilder.OngoingReading query = match(person);
        if (!conditions.isEmpty()) {
            Condition condition = conditions.get(0);
            for (Condition currentCondition : conditions) {
                if (currentCondition == condition) continue;
                condition = condition.and(currentCondition);
            }
            query = ((StatementBuilder.OngoingReadingWithoutWhere)query).where(condition);
        }

        return this.template.findAll(
            query.returning(person).build(),
            Person.class
        );
    }

    public Person findByIdOrKey(String id) {
        return this.personRepository.findByIdOrKey(id).orElseThrow(NotFoundException::new);
    }

    public void deleteById(String id) {
        Person person = this.personRepository.findById(id).orElseThrow(NotFoundException::new);
        this.personRepository.delete(person);
    }

}

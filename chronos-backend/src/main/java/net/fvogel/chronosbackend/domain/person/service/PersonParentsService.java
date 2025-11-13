package net.fvogel.chronosbackend.domain.person.service;

import net.fvogel.chronosbackend.domain.person.persistence.ChildOf;
import net.fvogel.chronosbackend.domain.person.persistence.Person;
import net.fvogel.chronosbackend.domain.person.persistence.PersonRepository;
import net.fvogel.chronosbackend.shared.exception.ConflictingDataException;
import net.fvogel.chronosbackend.shared.exception.InvalidDataException;
import net.fvogel.chronosbackend.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonParentsService {

    private static final Logger logger = LoggerFactory.getLogger(PersonParentsService.class);

    private final PersonRepository personRepository;
    private final Neo4jOperations template;

    public PersonParentsService(PersonRepository personRepository,
                                Neo4jOperations template) {
        this.personRepository = personRepository;
        this.template = template;
    }

    public Person addChildOf(Person person, ChildOf childOf) {
        String parentId = childOf.getParent().getId();

        // Invalid parent ID
        if (parentId == null) {
            throw new InvalidDataException();
        }

        // Parent (ID) already present in parents
        if (person.getParents().stream().anyMatch(currentChildOf -> currentChildOf.getParent().getId().equals(parentId))) {
            throw new ConflictingDataException();
        }

        // Load complete parent and set it -> allows to only specify the ID
        Person parent = this.personRepository.findById(parentId).orElseThrow(NotFoundException::new);
        childOf.setParent(parent);

        // Add & update
        person.getParents().add(childOf);
        this.personRepository.save(person);
        return this.personRepository.findById(person.getId()).orElseThrow(NotFoundException::new);
    }

    public Person updateChildOf(Person person, String parentId, ChildOf childOf) {
        // Find existing relation and remove it (required by SND)
        ChildOf existingChildOf = person.getParents().stream()
                .filter(currentChildOf -> currentChildOf.getParent().getId().equals(parentId))
                .findFirst()
                .orElseThrow(NotFoundException::new);
        deleteChildOf(person.getId(), parentId);

        // Transfer parent as it cannot be changed
        childOf.setParent(existingChildOf.getParent());
        person.getParents().add(childOf);

        this.template.save(person);
        return this.personRepository.findById(person.getId()).orElseThrow(NotFoundException::new);
    }

    public void deleteChildOf(String childId, String parentId) {
        this.personRepository.removeChild(childId, parentId);
    }

}

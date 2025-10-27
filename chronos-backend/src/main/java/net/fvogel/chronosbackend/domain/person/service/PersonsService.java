package net.fvogel.chronosbackend.domain.person.service;

import net.fvogel.chronosbackend.domain.person.persistence.Person;
import net.fvogel.chronosbackend.domain.person.persistence.PersonRepository;
import net.fvogel.chronosbackend.general.wikipedia.client.WikipediaApiClient;
import net.fvogel.chronosbackend.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class PersonsService {

    private static final Logger logger = LoggerFactory.getLogger(PersonsService.class);

    private PersonRepository personRepository;
    private WikipediaApiClient wikipediaApiClient;

    public PersonsService(PersonRepository personRepository,
                          WikipediaApiClient wikipediaApiClient) {
        this.personRepository = personRepository;
        this.wikipediaApiClient = wikipediaApiClient;
    }

    public Person save(Person entry) {
        return this.personRepository.save(entry);
    }

    public List<Person> saveAll(Collection<Person> persons) {
        return this.personRepository.saveAll(persons);
    }

    public List<Person> findAll() {
        return this.personRepository.findAll();
    }

    public List<Person> findBetween(String from, String to) {
        return this.personRepository.findBetween(from, to);
    }

    public Person findById(String id) {
        return this.personRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public void deleteById(String id) {
        Person person = this.personRepository.findById(id).orElseThrow(NotFoundException::new);
        this.personRepository.delete(person);
    }

//    public WikipediaSummary findRandom() {
//        // TODO: Make this working again, for any node type
//        Person randomPerson = this.personRepository.findAll().get(0);
//        return this.findWikipediaSummaryForPerson(randomPerson.getKey());
//    }

}

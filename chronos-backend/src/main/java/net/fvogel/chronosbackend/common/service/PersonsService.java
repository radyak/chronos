package net.fvogel.chronosbackend.common.service;

import net.fvogel.chronosbackend.common.exception.InvalidParameterException;
import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.model.Person;
import net.fvogel.chronosbackend.common.persistence.repo.PersonRepository;
import net.fvogel.chronosbackend.wikipedia.model.WikipediaSummary;
import net.fvogel.chronosbackend.wikipedia.service.WikipediaService;
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
    private WikipediaService wikipediaService;

    public PersonsService(PersonRepository personRepository,
                          WikipediaService wikipediaService) {
        this.personRepository = personRepository;
        this.wikipediaService = wikipediaService;
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

    public WikipediaSummary findWikipediaSummaryForPerson(String id) {
        // TODO: Make this working again
        Person person = this.findById(id);
//        if (person.getWikipediaPage() == null) {
            return this.wikipediaService.findWikipediaArticleSummary(person.getKey());
//        } else {
//            return this.wikipediaService.findWikipediaArticleSummary(person.getWikipediaPage());
//        }
    }

    public WikipediaSummary findWikipediaSummaryForTitle(String title) {
        if (title == null || title.length() < 3) {
            throw new InvalidParameterException();
        }
        return this.wikipediaService.findWikipediaArticleSummary(title);
    }

    public WikipediaSummary findRandom() {
        // TODO: Make this working again
        Person randomPerson = this.personRepository.findAll().get(0);
        return this.findWikipediaSummaryForPerson(randomPerson.getKey());
    }

}
